package coffee.amo.affixated.platform;

import chronosacaria.mcda.items.ArmorSetItem;
import chronosacaria.mcdw.bases.*;
import coffee.amo.affixated.Affixated;
import coffee.amo.affixated.affix.Affix;
import coffee.amo.affixated.affix.AffixHelper;
import coffee.amo.affixated.affix.AffixInstance;
import coffee.amo.affixated.affix.ItemExtension;
import coffee.amo.affixated.fabric.AffixatedFabric;
import coffee.amo.affixated.fabric.Components;
import coffee.amo.affixated.fabric.TrinketsHelper;
import coffee.amo.affixated.mixin.ItemStackMixin;
import coffee.amo.affixated.platform.services.IPlatformHelper;
import coffee.amo.affixated.util.ItemHelper;
import dev.architectury.platform.Platform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public void apply(AffixInstance instance, ItemStack stack, String slot, UUID uuid) {
        Affix affix = instance.getAffix();
        if (Platform.isModLoaded("trinkets")) {
            boolean isTrinket = TrinketsHelper.apply(stack, affix.getAttribute(), instance.getValue(), affix.getOperation(), slot, uuid, instance.getRarity().toString().toLowerCase().substring(instance.getRarity().toString().toLowerCase().indexOf(":") + 1));
            if (isTrinket) {
                stack.getOrCreateTagElement("affixes").put(affix.getId(), instance.toNbt());
                return;
            }
        }
        ((ItemExtension) (Object) stack).addDataAttributeModifier(affix.getAttribute(), new AttributeModifier(uuid, affix.getId(), instance.getValue(), instance.getAffix().getOperation()), ItemHelper.getDefaultSlot(stack));
        CompoundTag tag = new CompoundTag();
        tag.put(affix.getId(), instance.toNbt());
//        ((ListTag)AffixHelper.getOrCreateTagElement(stack, "affixes")).add(tag);
        if (stack.hasTag()) {
            if (stack.getTag().contains("affixes")) {
                stack.getTag().getList("affixes", 10).add(tag);
            } else {
                stack.getTag().put("affixes", new ListTag());
                stack.getTag().getList("affixes", 10).add(tag);
            }
            stack.getTag().putInt("AdminRerolls", 1);
        } else {
            stack.getOrCreateTag().put("affixes", new ListTag());
            stack.getTag().getList("affixes", 10).add(tag);
            stack.getTag().putInt("AdminRerolls", 1);
        }
    }

    @Override
    public void remove(ItemStack stack, Attribute attribute, UUID uuid) {
        if (Platform.isModLoaded("trinkets")) {
            boolean isTrinket = TrinketsHelper.remove(stack);
        }
    }

    @Override
    public List<String> getDefaultSlots(ItemStack stack) {
        Player player = AffixatedFabric.fakePlayer;
        List<String> slots = new ArrayList<>();
        if (Platform.isModLoaded("trinkets")) {
            if (TrinketsHelper.isTrinket(stack)) {
                slots.addAll(TrinketsHelper.getDefaultSlots(stack, player));
                return slots;
            }
        }
        String slot = ItemHelper.getDefaultSlot(stack).toString();
        if (stack.getItem() instanceof StandingAndWallBlockItem) {
            slot = "MISC";
        }
        if (Objects.equals(slot, "MAINHAND")) {
            if (stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem) {
                slot = "MELEE";
            } else if (stack.getItem() instanceof TridentItem) {
                slot = "TRIDENT";
            } else if (stack.getItem() instanceof ProjectileWeaponItem) {
                slot = "RANGED";
            } else {
                slot = "MISC";
            }
        }
        if (Objects.equals(slot, "OFFHAND")) {
            if (stack.getItem() instanceof ShieldItem) {
                slot = "SHIELD";
            } else {
                slot = "MISC";
            }
        }
        slots.add(slot);
        return slots;
    }

    @Override
    public void rerollInv(Player player) {
        int rerolls = Components.REROLL.get(player).getRerolls();
        Affixated.LOGGER.info("Rerolling inventory with " + rerolls + " rerolls for player " + player.getName().getString());
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem().equals(Items.AIR)) continue;
            if (stack.hasTag()) {
                if (stack.getOrCreateTag().contains("AdminRerolls")) continue;
            }
            AffixHelper.reroll(stack, player.level, player);
        }
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem().equals(Items.AIR)) continue;
            if (stack.hasTag()) {
                if (stack.getOrCreateTag().contains("AdminRerolls")) continue;
            }
            AffixHelper.reroll(stack, player.level, player);
        }
        if (Platform.isModLoaded("trinkets")) {
            TrinketsHelper.reroll(player);
        }
        Components.REROLL.get(player).setRerolls(rerolls + 1);

    }

    @Override
    public ItemStack rerollRelic(ItemStack stack) {
        if(!stack.getTag().contains("AdminRerolls")){
            ItemStack copyStack = new ItemStack(stack.getItem());
            stack.shrink(stack.getCount());
            copyStack.getTag().putInt("AdminRerolls", 1);
            return copyStack;
        }
        return stack;
    }

    @Override
    public boolean testBadlyCodedMod(ItemStack stack) {
        if (stack.getItem() instanceof ArmorSetItem) {
            return true;
        }
        if (stack.getItem() instanceof McdwAxe || stack.getItem() instanceof McdwBow || stack.getItem() instanceof McdwCrossbow || stack.getItem() instanceof McdwCustomWeaponBase || stack.getItem() instanceof McdwDagger
                || stack.getItem() instanceof McdwHammer
                || stack.getItem() instanceof McdwSpear || stack.getItem() instanceof McdwSword || stack.getItem() instanceof McdwWhip || stack.getItem() instanceof McdwShield || stack.getItem() instanceof McdwStaff || stack.getItem() instanceof McdwGlaive
                || stack.getItem() instanceof McdwGauntlet || stack.getItem() instanceof McdwLongbow || stack.getItem() instanceof McdwPick || stack.getItem() instanceof McdwDoubleAxe || stack.getItem() instanceof McdwScythe || stack.getItem() instanceof McdwSickle
                || stack.getItem() instanceof McdwSoulDagger || stack.getItem() instanceof McdwShortbow
        ) {
            return true;
        }
        return false;
    }
}

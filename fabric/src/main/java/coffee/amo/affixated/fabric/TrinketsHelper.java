package coffee.amo.affixated.fabric;

import coffee.amo.affixated.affix.AffixHelper;
import com.github.clevernucleus.dataattributes.api.item.ItemHelper;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class TrinketsHelper {
    public static Map<ResourceLocation, String> slotMap = new HashMap<>();

    public static boolean apply(ItemStack stack, Attribute attribute, double amount, AttributeModifier.Operation operation, String slot, UUID uuid, String substring) {
        if (stack.getItem() instanceof Trinket ti) {
            if (stack.getOrCreateTag().contains("TrinketAttributeModifiers")) {
                ListTag listTag = stack.getOrCreateTag().getList("TrinketAttributeModifiers", 9);
                setTrinketTag(stack, attribute, amount, operation, slot, uuid, listTag, substring);
            } else {
                ListTag listTag = new ListTag();
                setTrinketTag(stack, attribute, amount, operation, slot, uuid, listTag, substring);
            }
            return true;
        }
        return false;
    }

    public static boolean remove(ItemStack stack) {
        if (stack.getItem() instanceof Trinket) {
            stack.getOrCreateTag().remove("TrinketAttributeModifiers");
            if (stack.hasTag()) {
                if (stack.getTag().contains("TrinketAttributeModifiers")) {
                    stack.getTag().getList("TrinketAttributeModifiers", 9).forEach(tag -> {
                        int e = tag.getId();
                    });
                    stack.getTag().remove("TrinketAttributeModifiers");
                }
            }
            return true;
        }
        return false;
    }

    private static void setTrinketTag(ItemStack stack, Attribute attribute, double amount, AttributeModifier.Operation operation, String slot, UUID uuid, ListTag listTag, String substring) {
        AttributeModifier modifier = new AttributeModifier(uuid.toString(), amount, operation);
        CompoundTag tag = modifier.save();
        tag.putString("AttributeName", AffixHelper.getAttributeKey(attribute).get().getPath());
        tag.putString("Name", attribute.getDescriptionId());
        tag.putString("Slot", slot);
        listTag.add(tag);
        stack.getOrCreateTag().put("TrinketAttributeModifiers", listTag);
    }

    public static boolean isTrinket(ItemStack stack) {
        return stack.getItem() instanceof Trinket;
    }

    public static List<String> getDefaultSlots(ItemStack stack, Player player) {
        List<String> slots = new ArrayList<>();
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            for (Map.Entry<String, Map<String, TrinketInventory>> group : component.getInventory().entrySet()) {
                for (Map.Entry<String, TrinketInventory> inventory : group.getValue().entrySet()) {
                    TrinketInventory trinketInventory = inventory.getValue();
                    SlotType slotType = trinketInventory.getSlotType();
                    for (int i = 0; i < trinketInventory.getContainerSize(); i++) {
                        SlotReference ref = new SlotReference(trinketInventory, i);
                        boolean res = TrinketsApi.evaluatePredicateSet(slotType.getTooltipPredicates(), stack, ref, player);
                        boolean canInsert = TrinketSlot.canInsert(stack, ref, player);
                        if (res && canInsert) {
                            String slotId = slotType.getGroup() + "/" + slotType.getName();
                            slots.add(slotId);
                        }
                    }
                }
            }
        });
        return slots;
    }

    public static void reroll(Player player) {
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            component.getAllEquipped().forEach(stack -> {
                if(stack.getB().hasTag()){
                    if(stack.getB().getOrCreateTag().contains("AdminRerolls")) return;
                }
                AffixHelper.reroll(stack.getB(), player.level, player);
            });
        });
    }
}

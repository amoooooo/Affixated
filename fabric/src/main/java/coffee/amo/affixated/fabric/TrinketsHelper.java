package coffee.amo.affixated.fabric;

import coffee.amo.affixated.fabric.slots.AffixatedTrinketSlot;
import coffee.amo.affixated.slots.SlotHolder;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class TrinketsHelper {
    public static Map<ResourceLocation, String> slotMap = new HashMap<>();
    public static boolean apply(ItemStack stack, Attribute attribute, double amount, AttributeModifier.Operation operation, String slot, UUID uuid){
        if(stack.getItem() instanceof TrinketItem ti){
            if(stack.getOrCreateTag().contains("TrinketAttributeModifiers")){
                ListTag listTag = stack.getOrCreateTag().getList("TrinketAttributeModifiers", 9);
                AttributeModifier modifier = new AttributeModifier(uuid.toString(), amount, operation);
                CompoundTag tag = modifier.save();
                tag.putString("Slot", slot);
                listTag.add(tag);
                stack.getOrCreateTag().put("TrinketAttributeModifiers", listTag);
            } else {
                ListTag listTag = new ListTag();
                AttributeModifier modifier = new AttributeModifier(uuid.toString(), amount, operation);
                CompoundTag tag = modifier.save();
                tag.putString("Slot", slot);
                listTag.add(tag);
                stack.getOrCreateTag().put("TrinketAttributeModifiers", listTag);
            }
            return true;
        }
        return false;
    }

    public static boolean isTrinket(ItemStack stack){
        return stack.getItem() instanceof TrinketItem;
    }

    public static List<String> getDefaultSlots(ItemStack stack, Player player){
        List<String> slots = new ArrayList<>();
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            for(Map.Entry<String, Map<String, TrinketInventory>> group : component.getInventory().entrySet()){
                for(Map.Entry<String, TrinketInventory> inventory : group.getValue().entrySet()){
                    TrinketInventory trinketInventory = inventory.getValue();
                    SlotType slotType = trinketInventory.getSlotType();
                    for(int i = 0; i < trinketInventory.getContainerSize(); i++){
                        SlotReference ref = new SlotReference(trinketInventory, i);
                        boolean res = TrinketsApi.evaluatePredicateSet(slotType.getTooltipPredicates(), stack, ref, player);
                        boolean canInsert = TrinketSlot.canInsert(stack, ref, player);
                        if(res && canInsert){
                            slots.add(slotType.getName());
                        }
                    }
                }
            }
        });
        return slots;
    }
}

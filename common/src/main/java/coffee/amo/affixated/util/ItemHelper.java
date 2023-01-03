package coffee.amo.affixated.util;

import dev.architectury.extensions.ItemExtension;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemHelper {
    public static EquipmentSlot getDefaultSlot(ItemStack stack){
        return Mob.getEquipmentSlotForItem(stack);
    }

    public static boolean containsAllIn(List<String> list, List<String> other){
        for(String s : other){
            if(!list.contains(s)){
                return false;
            }
        }
        return true;
    }
}

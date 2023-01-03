package coffee.amo.affixated.forge;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.util.ICuriosHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CuriosHelper {

    public static boolean isCurio(ItemStack stack){
        return stack.getItem() instanceof ICurioItem;
    }

    public static boolean apply(ItemStack stack, Attribute attribute, double amount, AttributeModifier.Operation operation, String slot, UUID uuid){
        if (CuriosApi.getCuriosHelper().getCurio(stack).isPresent()) {
            CuriosApi.getCuriosHelper().getCurioTags(stack.getItem()).forEach(tag -> {
                if (tag.equals(slot)) {
                    CuriosApi.getCuriosHelper().addModifier(stack, attribute, uuid.toString(), uuid, amount, operation, slot);
                }
            });
            return true;
        }
        return false;
    }

    public static List<String> getSlots(ItemStack stack){
        List<String> slots = new ArrayList<>();
        if (CuriosApi.getCuriosHelper().getCurio(stack).isPresent()) {
            slots.addAll(CuriosApi.getCuriosHelper().getCurioTags(stack.getItem()));
        }
        return slots;
    }
}

package coffee.amo.affixated.platform;

import coffee.amo.affixated.affix.Affix;
import coffee.amo.affixated.affix.AffixInstance;
import coffee.amo.affixated.fabric.TrinketsHelper;
import coffee.amo.affixated.platform.services.IPlatformHelper;
import coffee.amo.affixated.util.ItemHelper;
import dev.architectury.platform.Platform;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public void apply(AffixInstance instance, ItemStack stack, String slot, UUID uuid) {
        Affix affix = instance.getAffix();
        if(Platform.isModLoaded("trinkets")){
            boolean isTrinket = TrinketsHelper.apply(stack, affix.getAttribute(), instance.getValue(), affix.getOperation(), slot, uuid);
            if(isTrinket){
                //stack.getOrCreateTagElement("affixes").put(affix.getId(), instance.toNbt());
                return;
            }
        }
        stack.addAttributeModifier(affix.getAttribute(), new AttributeModifier(uuid, affix.getId(), instance.getValue(), AttributeModifier.Operation.ADDITION), ItemHelper.getDefaultSlot(stack));
        stack.getOrCreateTagElement("affixes").put(affix.getId(), instance.toNbt());
    }

    @Override
    public List<String> getDefaultSlots(ItemStack stack, @Nullable Player player) {
        List<String> slots = new ArrayList<>();
        if(Platform.isModLoaded("trinkets")) {
            if(TrinketsHelper.isTrinket(stack)){
                slots.addAll(TrinketsHelper.getDefaultSlots(stack, player));
                return slots;
            }
        }
        slots.add(ItemHelper.getDefaultSlot(stack).toString());
        return slots;
    }
}

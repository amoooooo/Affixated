package coffee.amo.affixated.platform.services;

import coffee.amo.affixated.affix.AffixInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface IPlatformHelper {
    void apply(AffixInstance instance, ItemStack stack, String slot, UUID uuid);

    List<String> getDefaultSlots(ItemStack stack, @Nullable Player player);

}

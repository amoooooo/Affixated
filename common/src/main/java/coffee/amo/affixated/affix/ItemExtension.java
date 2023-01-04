package coffee.amo.affixated.affix;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

public interface ItemExtension {

    void addDataAttributeModifier(Attribute attribute, AttributeModifier attributeModifier, @Nullable EquipmentSlot equipmentSlot);
}

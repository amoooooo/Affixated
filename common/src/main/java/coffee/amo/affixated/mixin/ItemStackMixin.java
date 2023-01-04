package coffee.amo.affixated.mixin;

import coffee.amo.affixated.affix.AffixHelper;
import coffee.amo.affixated.affix.ItemExtension;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemExtension {
    public void addDataAttributeModifier(Attribute attribute, AttributeModifier attributeModifier, @Nullable EquipmentSlot equipmentSlot) {
        ItemStack _this = (ItemStack) (Object) this;
        _this.getOrCreateTag();
        if (!_this.getTag().contains("AttributeModifiers", 9)) {
            _this.getTag().put("AttributeModifiers", new ListTag());
        }

        ListTag listTag = _this.getTag().getList("AttributeModifiers", 10);
        CompoundTag compoundTag = attributeModifier.save();
        compoundTag.putString("AttributeName", AffixHelper.getKeyByValue(AffixHelper.ATTRIBUTE_HOLDER, attribute).toString());
        if (equipmentSlot != null) {
            compoundTag.putString("Slot", equipmentSlot.getName());
        }

        listTag.add(compoundTag);
    }
}

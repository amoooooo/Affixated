package coffee.amo.affixated.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AttributeModifier.class)
public interface AttributeModifierMixin {

    @Accessor
    double getAmount();

    @Accessor("amount")
    public void setAmount(double amount);
}

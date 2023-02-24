package coffee.amo.affixated.fabric.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.resources.ResourceLocation;

public class Components {
    public static final ComponentKey<RerollComponent> REROLL = ComponentRegistry.getOrCreate(new ResourceLocation("affixated", "reroll"), RerollComponent.class);
    public static final ComponentKey<ManaComponent> MANA = ComponentRegistry.getOrCreate(new ResourceLocation("affixated", "mana"), ManaComponent.class);

}

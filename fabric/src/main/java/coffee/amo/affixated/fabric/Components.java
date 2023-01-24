package coffee.amo.affixated.fabric;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class Components {
    public static final ComponentKey<RerollComponent> REROLL = ComponentRegistry.getOrCreate(new ResourceLocation("affixated", "reroll"), RerollComponent.class);


}

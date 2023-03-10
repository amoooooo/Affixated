package coffee.amo.affixated.fabric.components;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public class MyComponents implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(Components.REROLL, player -> new RerollComponentCounter(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(Components.MANA, player -> new ManaComponentContainer(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}

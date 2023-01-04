package coffee.amo.affixated.fabric;

import coffee.amo.affixated.AffixatedClient;
import net.fabricmc.api.ClientModInitializer;

public class AffixatedClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AffixatedClient.init();
    }
}

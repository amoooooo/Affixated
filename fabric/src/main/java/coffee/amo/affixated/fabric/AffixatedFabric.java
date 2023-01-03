package coffee.amo.affixated.fabric;

import coffee.amo.affixated.Affixated;
import coffee.amo.affixated.affix.AffixJsonListener;
import coffee.amo.affixated.fabric.slots.AffixatedTrinketSlot;
import coffee.amo.affixated.slots.SlotHolder;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;

public class AffixatedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Affixated.init();
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
            if(success){
                TrinketsHelper.slotMap.clear();
                TrinketsApi.getTrinketComponent(server.getPlayerList().getPlayers().get(0)).ifPresent(trinketComponent -> {
                    trinketComponent.getGroups().entrySet().forEach(entry -> {
                        SlotHolder.registerSlot(new AffixatedTrinketSlot(entry.getValue()));
                    });
                });
            }
        });
    }
}
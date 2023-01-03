package coffee.amo.affixated.forge;

import coffee.amo.affixated.forge.slots.AffixatedCurioSlot;
import coffee.amo.affixated.slots.SlotHolder;
import dev.architectury.platform.forge.EventBuses;
import coffee.amo.affixated.Affixated;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;

@Mod(Affixated.MOD_ID)
public class AffixatedForge {
    public AffixatedForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Affixated.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Affixated.init();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Affixated.MOD_ID)
    static
    class ForgeEventSubscriber {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onDatapackReload(OnDatapackSyncEvent event){
            if(event.getPlayer() == null){
                CuriosApi.getSlotHelper().getSlotTypes().forEach(slot -> {
                    SlotHolder.registerSlot(new AffixatedCurioSlot(slot));
                });
            }
        }
    }
}
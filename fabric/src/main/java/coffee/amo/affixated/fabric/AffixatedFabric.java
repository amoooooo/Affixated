package coffee.amo.affixated.fabric;

import coffee.amo.affixated.Affixated;
import coffee.amo.affixated.affix.AffixJsonListener;
import coffee.amo.affixated.fabric.slots.AffixatedTrinketSlot;
import coffee.amo.affixated.slots.SlotHolder;
import com.mojang.authlib.GameProfile;
import dev.architectury.event.events.common.LootEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class AffixatedFabric implements ModInitializer {
    public static Player fakePlayer;
    public static MinecraftServer server;

    @Override
    public void onInitialize() {
        Affixated.init();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            AffixatedFabric.server = server;
            fakePlayer = new FakePlayer(server.getLevel(Level.OVERWORLD), new GameProfile(null, "AffixatedFakePlayer"));
        });
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
            if(success){
                TrinketsHelper.slotMap.clear();
                TrinketsApi.getEntitySlots(server.getLevel(Level.OVERWORLD), EntityType.PLAYER).entrySet().forEach(entry -> {
                    entry.getValue().getSlots().entrySet().forEach(slot -> {
                        SlotHolder.registerSlot(new AffixatedTrinketSlot(slot.getValue()));
                    });
                });
            }
        });
    }
}
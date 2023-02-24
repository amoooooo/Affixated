package coffee.amo.affixated;

import coffee.amo.affixated.affix.*;
import coffee.amo.affixated.platform.Services;
import coffee.amo.affixated.slots.AffixatedVanillaSlot;
import coffee.amo.affixated.slots.SlotHolder;
import coffee.amo.affixated.util.ItemHelper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.CommandPerformEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.ReloadListenerRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Affixated {
    public static final String MOD_ID = "affixated";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ResourceLocation SYNC_PACKET = new ResourceLocation(MOD_ID, "sync_packet");

    public static void init() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, RarityJsonListener.INSTANCE);
        ReloadListenerRegistry.register(PackType.SERVER_DATA, AffixJsonListener.INSTANCE);
        setupAffixatedVanillaSlots();
        PlayerEvent.PLAYER_JOIN.register((player) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeNbt(Rarity.raritiesToList());
            buf.writeNbt(Affix.affixesToList());
            NetworkManager.sendToPlayer(player, SYNC_PACKET, buf);
            if(!player.getTags().contains("rerolled")){
                Services.PLATFORM.rerollInv(player);
            }
        });

        CommandRegistrationEvent.EVENT.register((dispatcher, dedicated, environment) -> {
            final LiteralArgumentBuilder<CommandSourceStack> affixated = LiteralArgumentBuilder.literal("affixated-reroll");
            affixated.executes(context -> {
                try {
                    context.getSource().getServer().getPlayerList().getPlayers().forEach(Services.PLATFORM::rerollInv);
                    return 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
            dispatcher.register(affixated);
        });

        CommandPerformEvent.EVENT.register((command) -> {
            if (command.toString().contains("reload")) {
                if(command.getResults().getContext().getSource().getPlayer() == null) return EventResult.pass();
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeNbt(Rarity.raritiesToList());
                buf.writeNbt(Affix.affixesToList());
                NetworkManager.sendToPlayer(command.getResults().getContext().getSource().getPlayer(), SYNC_PACKET, buf);
            }
            return EventResult.pass();
        });
    }

    private static void setupAffixatedVanillaSlots(){
        for (int i = 0; i < EquipmentSlot.values().length; i++) {
            EquipmentSlot slot = EquipmentSlot.values()[i];
            SlotHolder.registerSlot(new AffixatedVanillaSlot(slot));
        }
    }
}
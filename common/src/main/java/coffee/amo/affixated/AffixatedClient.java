package coffee.amo.affixated;

import coffee.amo.affixated.affix.AffixInstance;
import coffee.amo.affixated.affix.Rarity;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AffixatedClient {
    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, Affixated.SYNC_PACKET, (buf, context) -> {
            Rarity.listToRarities(Objects.requireNonNull(buf.readNbt()));
            AffixInstance.fromNbt(Objects.requireNonNull(buf.readNbt()));
            Affixated.LOGGER.info("Received affixated data from server");
        });
        ClientTooltipEvent.ITEM.register((stack, lines, flag) -> {
            if(stack.hasTag()){
                if(stack.getTag().contains("affixes")){
                    ListTag affixes = stack.getTag().getList("affixes", 10);
                    AtomicInteger i = new AtomicInteger();
                    affixes.forEach(tag -> {
                        i.getAndIncrement();
                        AffixInstance inst = AffixInstance.fromNbt((CompoundTag) tag);
                        if(inst != null){
                            String affix = "affixated.rarity." + inst.getRarity().toString().toLowerCase(Locale.ROOT);
                            if(!lines.get(1).contains(Component.translatable(affix))){
                                lines.add(1, Component.translatable(affix).withStyle(s -> s.withColor(AffixInstance.fromNbt(affixes.getCompound(0)).getRarity().color).withItalic(false)));
                            }
                            if(i.get() >= 1) return;
                        }
                    });
                    return;
                }
            }
        });
    }
}

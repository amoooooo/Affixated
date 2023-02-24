package coffee.amo.affixated.fabric.components;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ManaHelper {
    
    public static void manaTick(Player player){
        float regen = Components.MANA.get(player).getManaRegen();
        float currentMana = Components.MANA.get(player).getMana();
        float maxMana = Components.MANA.get(player).getMaxMana();
        if (currentMana < maxMana) {
            float toAdd = Math.min(regen, maxMana - currentMana);
            Components.MANA.get(player).addMana(toAdd);
            if(player instanceof ServerPlayer sp){
                sp.displayClientMessage(Component.literal("Mana: " + currentMana), true);
            }
        }
    }
}

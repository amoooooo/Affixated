package coffee.amo.affixated.fabric.mixin;

import coffee.amo.affixated.fabric.AffixatedFabric;
import coffee.amo.affixated.fabric.components.Components;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellCast;
import net.spell_engine.internals.SpellCasterEntity;
import net.spell_engine.internals.SpellHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Supplier;

@Mixin(SpellHelper.class)
public class SpellHelperMixin {

    @Inject(method = "performSpell", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void performSpell(Level world, Player player, ResourceLocation spellId, List<Entity> targets, ItemStack itemStack, SpellCast.Action action, InteractionHand hand, int remainingUseTicks, CallbackInfo ci, Spell spell, SpellCasterEntity caster, float progress, float channelMultiplier, boolean shouldPerformImpact, Supplier trackingPlayers, SpellHelper.AmmoResult ammoResult, Spell.Release.Target targeting, boolean released, float duration) {
        if(Components.MANA.get(player).getMana() - (spell.learn.tier *10) > 0){
            Components.MANA.get(player).removeMana(spell.learn.tier *10f);
        } else {
            player.displayClientMessage(Component.literal("Not enough mana!"), true);
            ci.cancel();
        }
    }
}

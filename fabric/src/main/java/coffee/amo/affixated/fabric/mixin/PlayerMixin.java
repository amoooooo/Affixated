package coffee.amo.affixated.fabric.mixin;

import coffee.amo.affixated.fabric.components.Components;
import coffee.amo.affixated.fabric.components.ManaHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void tick(CallbackInfo ci) {
        ManaHelper.manaTick((Player) (Object) this);
    }
}

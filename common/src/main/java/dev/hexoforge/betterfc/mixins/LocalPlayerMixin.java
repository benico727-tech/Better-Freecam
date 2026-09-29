package dev.hexoforge.betterfc.mixins;

import dev.hexoforge.betterfc.BetterFC;
import dev.hexoforge.betterfc.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.hexoforge.betterfc.BetterFC.MC;

import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
@SuppressWarnings("EqualsBetweenInconvertibleTypes")
public class LocalPlayerMixin {

    // Needed for Baritone compatibility.
    @Inject(method = "isControlledCamera", at = @At("HEAD"), cancellable = true)
    private void onIsCamera(CallbackInfoReturnable<Boolean> cir) {
        if (BetterFC.isEnabled() && this.equals(MC.player)) {
            cir.setReturnValue(true);
        }
    }

    // Makes rotation depend upon DetachedCamera rather than the player.
    @Inject(method = "getViewXRot", at = @At("HEAD"), cancellable = true)
    private void onGetViewXRot(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (BetterFC.isEnabled() && !BetterFC.isPlayerControlEnabled() && !ModConfig.get().allowInteractionsFromPlayer()) {
            cir.setReturnValue(BetterFC.getDetachedCamera().getViewXRot(partialTick));
        }
    }

    // Makes rotation depend upon DetachedCamera rather than the player.
    @Inject(method = "getViewYRot", at = @At("HEAD"), cancellable = true)
    private void onGetViewYRot(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (BetterFC.isEnabled() && !BetterFC.isPlayerControlEnabled() && !ModConfig.get().allowInteractionsFromPlayer()) {
            cir.setReturnValue(BetterFC.getDetachedCamera().getViewYRot(partialTick));
        }
    }
}

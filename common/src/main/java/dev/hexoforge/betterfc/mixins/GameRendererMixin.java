package dev.hexoforge.betterfc.mixins;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import dev.hexoforge.betterfc.BetterFC;
import dev.hexoforge.betterfc.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.hexoforge.betterfc.BetterFC.MC;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Hide hand in better_fc if showHand is disabled
    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void onRenderItemInHand(CallbackInfo ci) {
        if (BetterFC.isEnabled() && ModConfig.get().shouldHideHand()) {
            ci.cancel();
        }
    }

    // Better FC never renders the distracting block outline while BetterFC is active.
    @Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
    private void onShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
        if (BetterFC.isEnabled()) {
            cir.setReturnValue(false);
        }
    }

    // Makes mouse clicks come from the player rather than the better_fc entity when player control is enabled or if interaction mode is set to player.
    // Moved to Minecraft#pick in 26.1
    //? if <26.1 {
    /*@ModifyVariable(method = "pick(F)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
    private Entity onGetHitTargetSource(Entity entity) {
        if (BetterFC.isEnabled() && (BetterFC.isPlayerControlEnabled() || ModConfig.get().allowInteractionsFromPlayer())) {
            return MC.player;
        }
        return entity;
    }
    *///? }
}

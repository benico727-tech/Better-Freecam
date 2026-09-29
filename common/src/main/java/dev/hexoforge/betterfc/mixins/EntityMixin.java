package dev.hexoforge.betterfc.mixins;

import net.minecraft.world.entity.Entity;
import dev.hexoforge.betterfc.BetterFC;
import dev.hexoforge.betterfc.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.hexoforge.betterfc.BetterFC.MC;

@Mixin(Entity.class)
@SuppressWarnings("EqualsBetweenInconvertibleTypes")
public class EntityMixin {

    // Makes mouse input rotate the DetachedCamera.
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double rotation, double pitch, CallbackInfo ci) {
        if (BetterFC.isEnabled() && this.equals(MC.player) && !BetterFC.isPlayerControlEnabled()) {
            BetterFC.getDetachedCamera().turn(rotation, pitch);
            ci.cancel();
        }
    }

    // Prevents DetachedCamera from pushing/getting pushed by entities.
    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void onPushAwayFrom(Entity entity, CallbackInfo ci) {
        if (BetterFC.isEnabled() && (entity.equals(BetterFC.getDetachedCamera()) || this.equals(BetterFC.getDetachedCamera()))) {
            ci.cancel();
        }
    }

    // Freezes the player's position if freezePlayer is enabled.
    @Inject(method = "setDeltaMovement(DDD)V", at = @At("HEAD"), cancellable = true)
    private void onSetVelocity(CallbackInfo ci) {
        if (better_fc$shouldFreeze()) {
            ci.cancel();
        }
    }

    // Freezes the player's position if freezePlayer is enabled.
    @Inject(method = "moveRelative", at = @At("HEAD"), cancellable = true)
    private void onUpdateVelocity(CallbackInfo ci) {
        if (better_fc$shouldFreeze()) {
            ci.cancel();
        }
    }

    // Freezes the player's position if freezePlayer is enabled.
    @Inject(method = "setPos(DDD)V", at = @At("HEAD"), cancellable = true)
    private void onSetPosition(CallbackInfo ci) {
        if (better_fc$shouldFreeze()) {
            ci.cancel();
        }
    }

    // Freezes the player's position if freezePlayer is enabled.
    @Inject(method = "setPosRaw", at = @At("HEAD"), cancellable = true)
    private void onSetPos(CallbackInfo ci) {
        if (better_fc$shouldFreeze()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean better_fc$shouldFreeze() {
        return BetterFC.isEnabled() && this.equals(MC.player) && better_fc$allowFreeze();
    }

    @Unique
    private boolean better_fc$allowFreeze() {
        return ModConfig.get().shouldFreezePlayer() && !BetterFC.isPlayerControlEnabled();
    }
}

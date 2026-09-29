package dev.hexoforge.betterfc.mixins;

import net.minecraft.world.entity.LivingEntity;
import dev.hexoforge.betterfc.BetterFC;
import dev.hexoforge.betterfc.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.hexoforge.betterfc.BetterFC.MC;
import static dev.hexoforge.betterfc.config.model.FlightMode.CREATIVE;

@Mixin(LivingEntity.class)
@SuppressWarnings("EqualsBetweenInconvertibleTypes")
public abstract class LivingEntityMixin {

    @Shadow public abstract float getHealth();

    // Allows for the horizontal speed of creative flight to be configured separately from vertical speed.
    @Inject(method = "getFrictionInfluencedSpeed", at = @At("HEAD"), cancellable = true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        if (BetterFC.isEnabled() && ModConfig.get().getFlightMode().equals(CREATIVE) && this.equals(BetterFC.getDetachedCamera())) {
            cir.setReturnValue((float) (BetterFC.getDetachedCamera().getHorizontalSpeed() / 10) * (BetterFC.getDetachedCamera().isSprinting() ? 2 : 1));
        }
    }

    // Disables better_fc upon receiving damage if disableOnDamage is enabled.
    @Inject(method = "setHealth", at = @At("HEAD"))
    private void onSetHealth(float health, CallbackInfo ci) {
        if (BetterFC.isEnabled() && ModConfig.get().shouldDisableOnDamage() && this.equals(MC.player)) {
            if (!MC.player.isCreative() && getHealth() > health) {
                BetterFC.disableNextTick();
            }
        }
    }
}

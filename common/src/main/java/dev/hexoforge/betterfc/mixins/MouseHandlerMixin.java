package dev.hexoforge.betterfc.mixins;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.ScrollWheelHandler;
import dev.hexoforge.betterfc.BetterFC;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.hexoforge.betterfc.BetterFC.MC;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow @Final private ScrollWheelHandler scrollWheelHandler;

    /**
     * Replaces hotbar scrolling with BetterFC speed adjustment. GUI screens keep
     * their normal scrolling behavior.
     */
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void betterfc$onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (BetterFC.isEnabled() && MC.screen == null && BetterFC.getDetachedCamera() != null) {
            boolean discrete = MC.options.discreteMouseScroll().get();
            double sensitivity = MC.options.mouseWheelSensitivity().get();
            double adjustedHorizontal = (discrete ? Math.signum(horizontal) : horizontal) * sensitivity;
            double adjustedVertical = (discrete ? Math.signum(vertical) : vertical) * sensitivity;
            Vector2i scroll = scrollWheelHandler.onMouseScroll(adjustedHorizontal, adjustedVertical);
            int amount = scroll.y != 0 ? scroll.y : -scroll.x;

            BetterFC.getDetachedCamera().adjustSpeed(amount);
            ci.cancel();
        }
    }
}

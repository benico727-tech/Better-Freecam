package dev.hexoforge.betterfc.mixins;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import dev.hexoforge.betterfc.BetterFC;
import dev.hexoforge.betterfc.config.ModConfig;
import dev.hexoforge.betterfc.util.DetachedCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.hexoforge.betterfc.BetterFC.MC;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    // Prevents shadow being cast when Iris is enabled.
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onShouldRender(Entity entity, Frustum culler, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof DetachedCamera) {
            cir.setReturnValue(false);
        } else if (entity == MC.player && BetterFC.isEnabled() && ModConfig.get().shouldHidePlayer()) {
            cir.setReturnValue(false);
        }
    }
}

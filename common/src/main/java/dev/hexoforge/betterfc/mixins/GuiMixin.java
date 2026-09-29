package dev.hexoforge.betterfc.mixins;

import net.minecraft.client.gui.Gui;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import dev.hexoforge.betterfc.BetterFC;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=1.20.6 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
 //? } else if > 1.18.2 {
/*import com.mojang.blaze3d.vertex.PoseStack;
*///? }

import static dev.hexoforge.betterfc.BetterFC.MC;

@Mixin(Gui.class)
public class GuiMixin {
    // Makes HUD correspond to the player rather than the DetachedCamera.
    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void onGetCameraPlayer(CallbackInfoReturnable<Player> cir) {
        if (BetterFC.isEnabled()) {
            cir.setReturnValue(MC.player);
        }
    }

    // Don't render equipped-item overlays while BetterFC is active
    @Inject(method = "extractTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderTextureOverlay(
            //? if >=1.20.6 {
            GuiGraphicsExtractor graphics,
            //? } else if > 1.18.2
            //PoseStack poseStack,
            Identifier texture,
            float alpha,
            CallbackInfo ci) {
        if (BetterFC.isEnabled()) {
            ci.cancel();
        }
    }
}

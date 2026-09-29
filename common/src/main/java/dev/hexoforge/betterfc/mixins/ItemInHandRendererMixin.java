package dev.hexoforge.betterfc.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import dev.hexoforge.betterfc.BetterFC;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >= 1.21.11 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//? } else {
/*import net.minecraft.client.renderer.MultiBufferSource;
 *///? }

import static dev.hexoforge.betterfc.BetterFC.MC;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Unique private float better_fc$tickDelta;

    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getViewXRot(F)F"
            )
    )
    private float redirectGetViewXRot(LocalPlayer player, float partialTick) {
        return BetterFC.isEnabled() ? BetterFC.getDetachedCamera().getViewXRot(partialTick) : player.getViewXRot(partialTick);
    }

    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getViewYRot(F)F"
            )
    )
    private float redirectGetViewYRot(LocalPlayer player, float partialTick) {
        return BetterFC.isEnabled() ? BetterFC.getDetachedCamera().getViewYRot(partialTick) : player.getViewYRot(partialTick);
    }

    // Makes arm movement depend upon DetachedCamera movement rather than player movement.
    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;xBob:F",
                    opcode = Opcodes.GETFIELD)
    )
    private float redirectGetXBob(LocalPlayer player) {
        return BetterFC.isEnabled() ? BetterFC.getDetachedCamera().xBob : player.xBob;
    }

    // Makes arm movement depend upon DetachedCamera movement rather than player movement.
    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;xBobO:F",
                    opcode = Opcodes.GETFIELD)
    )
    private float redirectGetXBobO(LocalPlayer player) {
        return BetterFC.isEnabled() ? BetterFC.getDetachedCamera().xBobO : player.xBobO;
    }

    // Makes arm movement depend upon DetachedCamera movement rather than player movement.
    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;yBob:F",
                    opcode = Opcodes.GETFIELD)
    )
    private float redirectGetYBob(LocalPlayer player) {
        return BetterFC.isEnabled() ? BetterFC.getDetachedCamera().yBob : player.yBob;
    }

    // Makes arm movement depend upon DetachedCamera movement rather than player movement.
    @Redirect(
            method = "renderHandsWithItems",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;yBobO:F",
                    opcode = Opcodes.GETFIELD)
    )
    private float redirectGetYBobO(LocalPlayer player) {
        return BetterFC.isEnabled() ? BetterFC.getDetachedCamera().yBobO : player.yBobO;
    }

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"))
    private void storeTickDelta(float partialTick, PoseStack poseStack,
                                //? if >=1.21.11 {
                                SubmitNodeCollector nodeCollector,
                                //? } else
                                //MultiBufferSource.BufferSource vertexConsumers,
                                LocalPlayer player,
                                int packedLight,
                                CallbackInfo ci) {
        this.better_fc$tickDelta = partialTick;
    }

    // Makes arm shading depend upon DetachedCamera position rather than player position.
    @ModifyVariable(method = "renderHandsWithItems", at = @At("HEAD"), argsOnly = true)
    private int onRenderItemSetLight(int lightCoords) {
        if (BetterFC.isEnabled()) {
            return MC.getEntityRenderDispatcher().getPackedLightCoords(BetterFC.getDetachedCamera(), better_fc$tickDelta);
        }
        return lightCoords;
    }
}

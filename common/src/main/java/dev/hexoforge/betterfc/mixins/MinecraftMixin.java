package dev.hexoforge.betterfc.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import dev.hexoforge.betterfc.BetterFC;
import dev.hexoforge.betterfc.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.hexoforge.betterfc.BetterFC.MC;
import static dev.hexoforge.betterfc.config.ModBindings.KEY_TOGGLE;
import static dev.hexoforge.betterfc.config.ModBindings.KEY_TRIPOD_RESET;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    // Prevents attacks when allowInteract is disabled.
    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    //~ if >1.17.1 CallbackInfo -> 'CallbackInfoReturnable<Boolean>'
    private void onDoAttack(CallbackInfoReturnable<Boolean> ci) {
        if (better_fc$disableInteract()) {
            ci.cancel();
        }
    }

    // Prevents item pick when allowInteract is disabled.
    //~ if >=26.1 pickBlock -> pickBlockOrEntity
    @Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
    private void onDoItemPick(CallbackInfo ci) {
        if (better_fc$disableInteract()) {
            ci.cancel();
        }
    }


    // Makes mouse clicks come from the player rather than the better_fc entity when player control is enabled or if interaction mode is set to player.
    // Was GameRenderer#pick before 26.1
    //? if >=26.1 {
    @ModifyVariable(method = "pick(F)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
    private Entity onGetHitTargetSource(Entity entity) {
        if (BetterFC.isEnabled() && (BetterFC.isPlayerControlEnabled() || ModConfig.get().allowInteractionsFromPlayer())) {
            return MC.player;
        }
        return entity;
    }
    //? }

    // Prevents block breaking when allowInteract is disabled.
    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockBreaking(CallbackInfo ci) {
        if (better_fc$disableInteract()) {
            ci.cancel();
        }
    }

    // Prevents hotbar keys from changing selected slot when better_fc key is held
    @Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 2), cancellable = true)
    private void onHandleInputEvents(CallbackInfo ci) {
        if (KEY_TOGGLE.get().isDown() || KEY_TRIPOD_RESET.get().isDown()) {
            ci.cancel();
        }
    }

    // Disables better_fc if the player disconnects.
    //~ if >=1.20.6 '"clearLevel()V"' -> '"disconnect*"'
    @Inject(method = "disconnect*", at = @At(value = "HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        BetterFC.onDisconnect();
    }

    @Unique
    private static boolean better_fc$disableInteract() {
        return BetterFC.isEnabled() && !BetterFC.isPlayerControlEnabled() && ModConfig.get().shouldPreventInteractions();
    }
}

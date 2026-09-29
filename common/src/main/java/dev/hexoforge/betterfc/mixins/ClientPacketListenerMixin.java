package dev.hexoforge.betterfc.mixins;

import net.minecraft.client.multiplayer.ClientPacketListener;
import dev.hexoforge.betterfc.BetterFC;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    // Disables better_fc when the player respawns/switches dimensions.
    @Inject(method = "handleRespawn", at = @At("TAIL"))
    private void onPlayerRespawn(CallbackInfo ci) {
        if (BetterFC.isEnabled()) {
            BetterFC.toggle();
        }
    }
}

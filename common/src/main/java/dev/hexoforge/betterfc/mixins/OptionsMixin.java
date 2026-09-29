package dev.hexoforge.betterfc.mixins;

import net.minecraft.client.Options;
import dev.hexoforge.betterfc.BetterFC;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {

    // Prevents switching to third person in better_fc.
    @Inject(method = "setCameraType", at = @At("HEAD"), cancellable = true)
    private void onSetPerspective(CallbackInfo ci) {
        if (BetterFC.isEnabled()) {
            ci.cancel();
        }
    }
}

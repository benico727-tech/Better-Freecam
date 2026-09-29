package dev.hexoforge.betterfc.fabric;

import net.fabricmc.loader.api.FabricLoader;
import dev.hexoforge.betterfc.ModPlatform;

public class FabricPlatform implements ModPlatform {

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}

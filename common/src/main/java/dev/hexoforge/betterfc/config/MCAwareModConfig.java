package dev.hexoforge.betterfc.config;

import net.minecraft.world.level.block.Block;
import dev.hexoforge.betterfc.config.controller.ConfigControllerRegistry;
import dev.hexoforge.betterfc.config.model.ModConfigDTO;

/**
 * Extends {@link ModConfig} with Minecraft-aware features.
 */
public interface MCAwareModConfig extends ModConfig {

    static MCAwareModConfig get() {
        return ConfigControllerRegistry.get(ModConfigDTO.class).getConfig();
    }

    // FIXME: interface should not use MC classes
    boolean ignoreCollisionWith(Block block);
}

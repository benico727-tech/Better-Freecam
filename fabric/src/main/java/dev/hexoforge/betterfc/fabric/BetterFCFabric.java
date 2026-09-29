package dev.hexoforge.betterfc.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import dev.hexoforge.betterfc.BetterFC;
import dev.hexoforge.betterfc.config.ModBindings;
import dev.hexoforge.betterfc.config.ModConfig;

public class BetterFCFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfig.setup();
        ModBindings.forEach(KeyMappingHelper::registerKeyMapping);
        ClientTickEvents.START_CLIENT_TICK.register(BetterFC::preTick);
        ClientTickEvents.END_CLIENT_TICK.register(BetterFC::postTick);
    }
}

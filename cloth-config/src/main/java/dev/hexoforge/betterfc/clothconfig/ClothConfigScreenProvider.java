package dev.hexoforge.betterfc.clothconfig;

import net.minecraft.client.gui.screens.Screen;
import dev.hexoforge.betterfc.config.controller.ConfigControllerRegistry;
import dev.hexoforge.betterfc.config.gui.ConfigScreenProvider;
import dev.hexoforge.betterfc.config.gui.OptionalProvider;
import dev.hexoforge.betterfc.config.model.ModConfigDTO;
import org.jetbrains.annotations.Nullable;

import static java.lang.Thread.currentThread;

public class ClothConfigScreenProvider implements ConfigScreenProvider, OptionalProvider {

    private @Nullable ModConfigScreenFactory factory;

    /**
     * Init {@link #factory} lazily, so that {@link ModConfigScreenFactory} is not class-loaded immediately.
     * <p>
     * This gives consumers an opportunity to test {@link ClothConfigScreenProvider#isAvailable()} before use.
     * @return {@link #factory} initialized
     */
    private ModConfigScreenFactory factory() {
        if (factory == null) {
            factory = new ModConfigScreenFactory(ConfigControllerRegistry.get(ModConfigDTO.class));
        }
        return factory;
    }

    @Override
    public String getName() {
        return "Cloth Config BetterFC GUI";
    }

    @Override
    public Screen getConfigScreen(@Nullable Screen parent) {
        return factory().getConfigScreen(parent);
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder", false, currentThread().getContextClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }
}

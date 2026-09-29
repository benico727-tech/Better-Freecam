package dev.hexoforge.betterfc.config;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import dev.hexoforge.betterfc.BetterFC;
import dev.hexoforge.betterfc.config.gui.ConfigScreenProvider;
import dev.hexoforge.betterfc.config.keys.BetterFCKeyMapping;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static dev.hexoforge.betterfc.config.keys.BetterFCKeyMappingBuilder.builder;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;

public enum ModBindings {

    KEY_TOGGLE(() -> builder("toggle")
            .action(BetterFC::toggle)
            .holdAction(BetterFC::activateTripodHandler)
            .defaultKey(GLFW_KEY_F4)
            .build()),
    KEY_CONTROL_PLAYER(() -> builder("controlPlayer.toggle")
            .action(BetterFC::switchControls)
            .build()),
    KEY_TRIPOD_RESET(() -> builder("tripod.reset")
            .holdAction(BetterFC::resetTripodHandler)
            .build()),
    KEY_CONFIG_GUI(() -> builder("config.open")
            .action(ConfigScreenProvider.provider()::openConfigScreen)
            .build());

    private static final Logger LOGGER = LoggerFactory.getLogger(ModBindings.class);

    private final Supplier<BetterFCKeyMapping> lazyMapping;

    ModBindings(Supplier<BetterFCKeyMapping> mappingSupplier) {
        lazyMapping = Suppliers.memoize(mappingSupplier);
    }

    /**
     * Lazily get the actual {@link BetterFCKeyMapping} represented by this enum value.
     * <p>
     * Values are constructed if they haven't been already.
     *
     * @return the actual {@link BetterFCKeyMapping}.
     */
    public BetterFCKeyMapping get() {
        return lazyMapping.get();
    }

    /**
     * Calls {@code action} using each {@link BetterFCKeyMapping} owned by this enum.
     * <p>
     * Values are constructed if they haven't been already.
     * <p>
     * Static implementation of {@link Iterable#forEach(Consumer)}.
     */
    public static void forEach(@NotNull Consumer<BetterFCKeyMapping> action) {
        Objects.requireNonNull(action);
        iterator().forEachRemaining(action);
    }

    /**
     * Static implementation of {@link Iterable#iterator()}.
     */
    public static @NotNull Iterator<BetterFCKeyMapping> iterator() {
        return stream().iterator();
    }

    /**
     * Static implementation of {@link Iterable#spliterator()}.
     */
    public static @NotNull Spliterator<BetterFCKeyMapping> spliterator() {
        return stream().spliterator();
    }

    /**
     * Static implementation of {@link Collection#stream()}.
     */
    public static @NotNull Stream<BetterFCKeyMapping> stream() {
        return Arrays.stream(values()).map(ModBindings::get);
    }

    private static Logger logger() {
        return LOGGER;
    }
}

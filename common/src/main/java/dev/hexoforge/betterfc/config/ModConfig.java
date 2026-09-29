package dev.hexoforge.betterfc.config;

import dev.hexoforge.betterfc.config.controller.ConfigControllerRegistry;
import dev.hexoforge.betterfc.config.model.FlightMode;
import dev.hexoforge.betterfc.config.model.ModConfigDTO;
import dev.hexoforge.betterfc.config.model.Perspective;

public interface ModConfig {

    /**
     * Called once, early during mod initialization.
     * Will load config from disk and perform internal setup.
     */
    static void setup() {
        ConfigControllerRegistry.init();
        ConfigControllerRegistry.get(ModConfigDTO.class).load();
    }

    static ModConfig get() {
        return ConfigControllerRegistry.get(ModConfigDTO.class).getConfig();
    }

    FlightMode getFlightMode();

    double getHorizontalSpeed();

    double getVerticalSpeed();

    boolean ignoreAllCollision();

    boolean shouldCheckInitialCollision();

    Perspective getInitialPerspective();

    boolean shouldShowPlayer();

    default boolean shouldHidePlayer() {
        return !shouldShowPlayer();
    }

    boolean shouldShowHand();

    default boolean shouldHideHand() {
        return !shouldShowHand();
    }

    boolean isFullBrightEnabled();

    boolean shouldShowSubmersionFog();

    default boolean shouldHideSubmersionFog() {
        return !shouldShowSubmersionFog();
    }

    boolean shouldDisableOnDamage();

    boolean shouldFreezePlayer();

    boolean shouldPreventInteractions();

    boolean allowInteractionsFromCamera();

    boolean allowInteractionsFromPlayer();

    boolean isRestrictedOnServer(String serverIp);

    boolean shouldNotifyBetterFC();

    boolean shouldNotifyTripod();
}

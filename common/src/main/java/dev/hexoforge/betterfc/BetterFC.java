package dev.hexoforge.betterfc;

import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import dev.hexoforge.betterfc.config.ModBindings;
import dev.hexoforge.betterfc.config.ModConfig;
import dev.hexoforge.betterfc.config.keys.Tickable;
import dev.hexoforge.betterfc.tripod.TripodRegistry;
import dev.hexoforge.betterfc.tripod.TripodSlot;
import dev.hexoforge.betterfc.util.DetachedCamera;
import dev.hexoforge.betterfc.util.CameraPosition;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

//? if >=1.21.11 {
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
//? } else {
/*import net.minecraft.client.player.Input;
*///? }

public class BetterFC {

    public static final Minecraft MC = Minecraft.getInstance();
    public static final String MOD_ID = "better_fc";

    private static boolean better_fcEnabled = false;
    private static boolean tripodEnabled = false;
    private static boolean playerControlEnabled = false;
    private static boolean disableNextTick = false;
    private static final TripodRegistry tripods = new TripodRegistry();
    private static TripodSlot activeTripod = TripodSlot.NONE;
    private static DetachedCamera detachedCamera;
    private static CameraType rememberedF5 = null;

    @ApiStatus.Internal
    public static void preTick(Minecraft mc) {
        // Disable if the previous tick asked us to,
        // or BetterFC is restricted on the current server
        if ((disableNextTick || isRestrictedOnServer()) && isEnabled()) {
            toggle();
        }
        disableNextTick = false;

        if (isEnabled()) {
            // Prevent player from being controlled when better_fc is enabled
            if (mc.player != null && mc.player.input instanceof KeyboardInput && !isPlayerControlEnabled()) {
                //? if >=1.21.11 {
                ClientInput input = new ClientInput();
                Input keyPresses = mc.player.input.keyPresses;
                input.keyPresses = new Input(
                        false,
                        false,
                        false,
                        false,
                        false,
                        keyPresses.shift(),
                        false
                );
                //? } else {
                /*Input input = new Input();
                input.keyPresses.shift() = mc.player.input.keyPresses.shift();
                *///? }
                mc.player.input = input;
            }
        }
    }

    @ApiStatus.Internal
    public static void postTick(Minecraft mc) {
        ModBindings.forEach(Tickable::tick);
    }

    @ApiStatus.Internal
    public static void onDisconnect() {
        if (isEnabled()) {
            toggle();
        }
        tripods.clear();
    }

    @ApiStatus.Internal
    public static boolean activateTripodHandler() {
        boolean activated = false;
        for (KeyMapping combo : MC.options.keyHotbarSlots) {
            while (combo.consumeClick()) {
                toggleTripod(TripodSlot.ofKeyCode(combo.getDefaultKey().getValue()));
                activated = true;
            }
        }
        return activated;
    }

    @ApiStatus.Internal
    public static boolean resetTripodHandler() {
        boolean reset = false;
        for (KeyMapping key : MC.options.keyHotbarSlots) {
            while (key.consumeClick()) {
                resetCamera(TripodSlot.ofKeyCode(key.getDefaultKey().getValue()));
                reset = true;
            }
        }
        return reset;
    }

    @ApiStatus.AvailableSince("0.3.1")
    public static void toggle() {
        if (isRestrictedOnServer()) {
            if (ModConfig.get().shouldNotifyBetterFC()) {
                sendOverlayMessage(Component.translatable("better_fc.msg.restricted.server", MC.getCurrentServer().ip));
            }
            return;
        }

        if (tripodEnabled) {
            toggleTripod(activeTripod);
            return;
        }

        if (better_fcEnabled) {
            onDisableBetterFC();
        } else {
            onEnableBetterFC();
        }
        better_fcEnabled = !better_fcEnabled;
        if (!better_fcEnabled) {
            onDisabled();
        }
    }

    private static void toggleTripod(TripodSlot tripod) {
        if (tripod == TripodSlot.NONE) {
            return;
        }

        if (isRestrictedOnServer()) {
            if (ModConfig.get().shouldNotifyTripod()) {
                sendOverlayMessage(Component.translatable("better_fc.msg.restricted.server", MC.getCurrentServer().ip));
            }
            return;
        }

        if (tripodEnabled) {
            if (activeTripod == tripod) {
                onDisableTripod();
                tripodEnabled = false;
            } else {
                onDisableTripod();
                onEnableTripod(tripod);
            }
        } else {
            if (better_fcEnabled) {
                toggle();
            }
            onEnableTripod(tripod);
            tripodEnabled = true;
        }
        if (!tripodEnabled) {
            onDisabled();
        }
    }

    @ApiStatus.AvailableSince("1.1.8")
    public static void switchControls() {
        if (!isEnabled()) {
            return;
        }

        if (playerControlEnabled) {
            detachedCamera.input = new KeyboardInput(MC.options);
        } else {
            MC.player.input = new KeyboardInput(MC.options);
            //~ if >=1.21.11 Input -> ClientInput
            detachedCamera.input = new ClientInput();
        }
        playerControlEnabled = !playerControlEnabled;
    }

    private static void onEnableTripod(TripodSlot tripod) {
        onEnable();

        CameraPosition position = tripods.get(tripod);
        boolean chunkLoaded = false;
        if (position != null) {
            ChunkPos chunkPos = position.getChunkPos();
            //~ if >=26.0 'chunkPos.x, chunkPos.z' -> 'chunkPos.x(), chunkPos.z()'
            chunkLoaded = MC.level.getChunkSource().hasChunk(chunkPos.x(), chunkPos.z());
        }

        if (!chunkLoaded) {
            resetCamera(tripod);
            position = null;
        }

        detachedCamera = new DetachedCamera(-420 - tripod.ordinal());
        if (position == null) {
            moveToPlayer();
        } else {
            moveToPosition(position);
        }

        detachedCamera.spawn();
        MC.setCameraEntity(detachedCamera);
        activeTripod = tripod;

        if (ModConfig.get().shouldNotifyTripod()) {
            sendOverlayMessage(Component.translatable("better_fc.msg.tripod.open", tripod));
        }
    }

    private static void onDisableTripod() {
        tripods.put(activeTripod, new CameraPosition(detachedCamera));
        onDisable();

        if (MC.player != null) {
            if (ModConfig.get().shouldNotifyTripod()) {
                sendOverlayMessage(Component.translatable("better_fc.msg.tripod.close", activeTripod));
            }
        }
        activeTripod = TripodSlot.NONE;
    }

    private static void onEnableBetterFC() {
        onEnable();
        detachedCamera = new DetachedCamera(-420);
        moveToPlayer();
        detachedCamera.spawn();
        MC.setCameraEntity(detachedCamera);

        if (ModConfig.get().shouldNotifyBetterFC()) {
            sendOverlayMessage(Component.translatable("better_fc.msg.enabled"));
        }
    }

    private static void onDisableBetterFC() {
        onDisable();

        if (MC.player != null) {
            if (ModConfig.get().shouldNotifyBetterFC()) {
                sendOverlayMessage(Component.translatable("better_fc.msg.disabled"));
            }
        }
    }

    private static void onEnable() {
        MC.smartCull = false;

        rememberedF5 = MC.options.getCameraType();
        if (MC.gameRenderer.getMainCamera().isDetached()) {
            MC.options.setCameraType(CameraType.FIRST_PERSON);
        }
    }

    private static void onDisable() {
        MC.smartCull = true;
        MC.setCameraEntity(MC.player);
        playerControlEnabled = false;
        detachedCamera.despawn();
        //~ if >=1.21.11 Input -> ClientInput
        detachedCamera.input = new ClientInput();
        detachedCamera = null;

        if (MC.player != null) {
            MC.player.input = new KeyboardInput(MC.options);
        }
    }

    private static void onDisabled() {
        if (rememberedF5 != null) {
            MC.options.setCameraType(rememberedF5);
        }
    }

    private static void resetCamera(TripodSlot tripod) {
        if (tripodEnabled && activeTripod != TripodSlot.NONE && activeTripod == tripod && detachedCamera != null) {
            moveToPlayer();
        } else {
            tripods.put(tripod, null);
        }

        if (ModConfig.get().shouldNotifyTripod()) {
            sendOverlayMessage(Component.translatable("better_fc.msg.tripod.reset", tripod));
        }
    }

    /** Send a message to be shown over the action bar. */
    private static void sendOverlayMessage(Component message) {
        //~ if >=26.0 'displayClientMessage(message, true)' -> 'sendOverlayMessage(message)'
        Optional.ofNullable(MC.player).ifPresent(player -> player.sendOverlayMessage(message));
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.3")
    public static void moveToEntity(@Nullable Entity entity) {
        if (detachedCamera == null) {
            return;
        }
        if (entity == null) {
            moveToPlayer();
            return;
        }
        detachedCamera.copyPosition(entity);
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.3")
    public static void moveToPosition(@Nullable CameraPosition position) {
        if (detachedCamera == null) {
            return;
        }
        if (position == null) {
            moveToPlayer();
            return;
        }
        detachedCamera.applyPosition(position);
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.3")
    public static void moveToPlayer() {
        if (detachedCamera == null) {
            return;
        }
        detachedCamera.copyPosition(MC.player);
        detachedCamera.applyPerspective(
                ModConfig.get().getInitialPerspective(),
                ModConfig.get().shouldCheckInitialCollision()
        );
    }

    @ApiStatus.AvailableSince("0.4.0")
    public static DetachedCamera getDetachedCamera() {
        return detachedCamera;
    }

    @ApiStatus.AvailableSince("1.2.3")
    public static void disableNextTick() {
        disableNextTick = true;
    }

    @ApiStatus.AvailableSince("0.2.2")
    public static boolean isEnabled() {
        return better_fcEnabled || tripodEnabled;
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.0.0")
    public static boolean isPlayerControlEnabled() {
        return playerControlEnabled;
    }

    @ApiStatus.Experimental
    @ApiStatus.AvailableSince("1.2.4")
    public static boolean isRestrictedOnServer() {
        ServerData server = MC.getCurrentServer();
        return server != null && !MC.hasSingleplayerServer()
                && ModConfig.get().isRestrictedOnServer(server.ip);
    }
}

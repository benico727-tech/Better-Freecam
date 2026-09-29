package dev.hexoforge.betterfc.config.keys;

import com.mojang.blaze3d.platform.InputConstants;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;

public class BetterFCKeyMappingBuilder {
    private final String translationKey;
    private InputConstants.Type type = InputConstants.Type.KEYSYM;
    private int keyCode = GLFW_KEY_UNKNOWN;
    private Runnable action;
    private HoldAction holdAction;
    private long maxTicks = 10;

    private BetterFCKeyMappingBuilder(String translationKey) {
        this.translationKey = translationKey;
    }

    /**
     * Start building a {@link BetterFCKeyMapping key} with the translation key provided.
     *
     * @param translationKey key to be appended onto {@code "key.better_fc."}
     * @return a {@link BetterFCKeyMapping} builder
     */
    public static BetterFCKeyMappingBuilder builder(String translationKey) {
        return new BetterFCKeyMappingBuilder(translationKey);
    }

    public BetterFCKeyMappingBuilder type(InputConstants.Type type) {
        this.type = type;
        return this;
    }

    public BetterFCKeyMappingBuilder maxHoldTicks(long ticks) {
        this.maxTicks = ticks;
        return this;
    }

    public BetterFCKeyMappingBuilder defaultKey(int keyCode) {
        this.keyCode = keyCode;
        return this;
    }

    public BetterFCKeyMappingBuilder action(Runnable action) {
        this.action = action;
        return this;
    }

    public BetterFCKeyMappingBuilder holdAction(HoldAction action) {
        holdAction = action;
        return this;
    }

    /**
     * Build the {@link BetterFCKeyMapping key mapping}.
     * <p>
     * If an {@link #action(Runnable) action} was defined, it will be run when the key is <strong>pressed</strong>.
     * <p>
     * If a {@link #holdAction(HoldAction) hold action} was defined, it will be run while the key is <strong>held</strong>
     * (each tick).
     * <p>
     * If both were defined, a {@link BetterFCComboKeyMapping combo key} is provided where the {@link #holdAction(HoldAction) hold action}
     * is run as normal and the {@link #action(Runnable) action} is run when the key is <strong>released</strong>.
     * <br>
     * If the key was held for {@link #maxHoldTicks(long) max hold ticks} or longer (default 10) then {@link #action(Runnable) action}
     * is <strong>not run</strong>. It is also not run if any {@link #holdAction(HoldAction) hold action} returned
     * {@code true} since the key last released.
     * @return the {@link BetterFCKeyMapping keybind}.
     */
    public BetterFCKeyMapping build() {
        if (action != null && holdAction != null) {
            return new BetterFCComboKeyMapping(translationKey, type, keyCode, action, holdAction, maxTicks);
        }
        if (action != null) {
            return new BetterFCKeyMapping(translationKey, type, keyCode, self -> {
                while (self.consumeClick()) {
                    action.run();
                }
            });
        }
        if (holdAction != null) {
            return new BetterFCKeyMapping(translationKey, type, keyCode, self -> {
                if (self.isDown()) {
                    holdAction.run();
                }
            });
        }
        throw new IllegalStateException("No action defined.");
    }
}

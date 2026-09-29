package dev.hexoforge.betterfc.config.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class BetterFCKeyMapping extends KeyMapping implements Tickable {

    //? if >=1.21.11 {
    private static final Category BETTER_FC_CATEGORY = Category.register(Identifier.fromNamespaceAndPath("better_fc", "controls"));
    //? } else
    //private static final String BETTER_FC_CATEGORY = "key.category.better_fc.controls";

    private final Consumer<BetterFCKeyMapping> onTick;

    /**
     * @apiNote should only be used if overriding {@link #tick()}
     */
    protected BetterFCKeyMapping(String translationKey, InputConstants.Type type, int code) {
        this(translationKey, type, code, null);
    }

    BetterFCKeyMapping(String translationKey, InputConstants.Type type, int code, Consumer<BetterFCKeyMapping> onTick) {
        super("key.better_fc." + translationKey, type, code, BETTER_FC_CATEGORY);
        this.onTick = onTick;
    }

    @Override
    public void tick() {
        onTick.accept(this);
    }

    /**
     * Reset whether the key was pressed.
     *
     * @implNote Cannot use {@link KeyMapping#release()} because it doesn't work as expected.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public void reset() {
        while (consumeClick()) {}
    }
}

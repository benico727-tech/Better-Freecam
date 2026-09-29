package dev.hexoforge.betterfc.config.model;

import net.minecraft.world.level.block.Block;
import dev.hexoforge.betterfc.config.model.ModConfigDTO.CollisionConfig;

import static dev.hexoforge.betterfc.config.model.CollisionPredicateBuilder.builder;
import static dev.hexoforge.betterfc.config.model.CollisionPredicates.*;

@FunctionalInterface
public interface CollisionPredicate {

    boolean shouldIgnore(Block block);

    static CollisionPredicate create(CollisionConfig config) {
        if (config.ignoreAll) return IGNORE;

        CollisionPredicateBuilder builder = builder();
        if (config.ignoreTransparent) builder.matching(IGNORE_TRANSPARENT);
        if (config.ignoreOpenable) builder.matching(IGNORE_OPENABLE);
        if (config.ignoreCustom) builder.matching(whitelist(config.whitelist));
        return builder.build();
    }
}

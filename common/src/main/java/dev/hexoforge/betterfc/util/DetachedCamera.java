package dev.hexoforge.betterfc.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec2;
import dev.hexoforge.betterfc.config.ModConfig;
import dev.hexoforge.betterfc.config.model.Perspective;
import org.jetbrains.annotations.ApiStatus;
//~ if >=1.21.11 Input -> ClientInput
import net.minecraft.client.player.ClientInput;
//? if >=1.20.6
import net.minecraft.core.Holder;

import java.util.UUID;

import static dev.hexoforge.betterfc.BetterFC.MC;

@ApiStatus.Internal
@ApiStatus.AvailableSince("0.4.0")
public class DetachedCamera extends AbstractClientPlayer {
    private static final double MIN_SCROLL_SPEED = 0.0;
    private static final double MAX_SCROLL_SPEED = 25.0;
    private static final double SCROLL_SPEED_STEP = 0.25;

    //~ if >=1.21.11 Input -> ClientInput
    public ClientInput input;
    public float yBob;
    public float xBob;
    public float yBobO;
    public float xBobO;
    private double scrollHorizontalSpeed = Double.NaN;

    public DetachedCamera(int id) {
        super(MC.level, new GameProfile(UUID.randomUUID(), "DetachedCamera"));

        setId(id);
        setPose(Pose.SWIMMING);
        getAbilities().flying = true;
        input = new KeyboardInput(MC.options);
    }

    @Override
    public void tick() {
        input.tick(
            //? if <1.21.11
            //false // isMovingSlowly
            //? if <1.21.11 && >=1.19
            //, 0.3F // sneakSpeedMultiplier
        );
        doMotion();
        super.tick();
    }

    @Override
    public void copyPosition(Entity entity) {
        applyPosition(new CameraPosition(entity));
    }

    public void applyPosition(CameraPosition position) {
        //~ if >=1.21.11 moveTo -> snapTo
        snapTo(position.x, position.y, position.z, position.yaw, position.pitch);
        xBob = getXRot();
        yBob = getYRot();
        xBobO = xBob; // Prevents camera from rotating upon entering better_fc.
        yBobO = yBob;
    }

    // Mutate the position and rotation based on perspective
    // If checkCollision is true, move as far as possible without colliding
    public void applyPerspective(Perspective perspective, boolean checkCollision) {
        CameraPosition position = new CameraPosition(this);

        switch (perspective) {
            case INSIDE:
                // No-op
                break;
            case FIRST_PERSON:
                // Move just in front of the player's eyes
                moveForwardUntilCollision(position, 0.4, checkCollision);
                break;
            case THIRD_PERSON_MIRROR:
                // Invert the rotation and fallthrough into the THIRD_PERSON case
                position.mirrorRotation();
            case THIRD_PERSON:
                // Move back as per F5 mode
                moveForwardUntilCollision(position, -4.0, checkCollision);
                break;
        }
    }

    // Move DetachedCamera forward using CameraPosition.moveForward.
    // If checkCollision is true, stop moving forward before hitting a collision.
    // Return true if successfully able to move.
    private boolean moveForwardUntilCollision(CameraPosition position, double distance, boolean checkCollision) {
        if (!checkCollision) {
            position.moveForward(distance);
            applyPosition(position);
            return true;
        }
        return moveForwardUntilCollision(position, distance);
    }

    // Same as above, but always check collision.
    private boolean moveForwardUntilCollision(CameraPosition position, double maxDistance) {
        boolean negative = maxDistance < 0;
        maxDistance = negative ? -1 * maxDistance : maxDistance;
        double increment = 0.1;

        // Move forward by increment until we reach maxDistance or hit a collision
        for (double distance = 0.0; distance < maxDistance; distance += increment) {
            CameraPosition oldPosition = new CameraPosition(this);

            position.moveForward(negative ? -1 * increment : increment);
            applyPosition(position);

            if (!wouldNotSuffocateAtTargetPose(getPose())) {
                // Revert to last non-colliding position and return whether we were unable to move at all
                applyPosition(oldPosition);
                return distance > 0;
            }
        }

        return true;
    }

    private ClientLevel getClientLevel() {
        //~ if >=1.20.6 'clientLevel' -> '(ClientLevel) level()'
        return (ClientLevel) level();
    }

    public void spawn() {
        //~ if >=1.20.6 'putNonPlayerEntity(getId(), this)' -> 'addEntity(this)'
        getClientLevel().addEntity(this);
    }

    public void despawn() {
        getClientLevel().removeEntity(getId(), RemovalReason.DISCARDED);
    }

    // Prevents fall damage sound when DetachedCamera touches ground with noClip disabled.
    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    // Needed for hand swings to be shown in better_fc since the player is replaced by DetachedCamera in HeldItemRenderer.renderItem()
    @Override
    public float getAttackAnim(float tickDelta) {
        return MC.player.getAttackAnim(tickDelta);
    }

    // Needed for item use animations to be shown in better_fc since the player is replaced by DetachedCamera in HeldItemRenderer.renderItem()
    @Override
    public int getUseItemRemainingTicks() {
        return MC.player.getUseItemRemainingTicks();
    }

    // Also needed for item use animations to be shown in better_fc.
    @Override
    public boolean isUsingItem() {
        return MC.player.isUsingItem();
    }

    // Prevents slow down from ladders/vines.
    @Override
    public boolean onClimbable() {
        return false;
    }

    // Prevents slow down from water.
    @Override
    public boolean isInWater() {
        return false;
    }

    // Makes night vision apply to DetachedCamera when Iris is enabled.
    @Override
    //~ if >=1.20.6 'MobEffect effect' -> 'Holder<MobEffect> effect'
    public MobEffectInstance getEffect(Holder<MobEffect> effect) {
        return MC.player.getEffect(effect);
    }

    // Prevents pistons from moving DetachedCamera when collision.ignoreAll is enabled.
    @Override
    public PushReaction getPistonPushReaction() {
        return ModConfig.get().ignoreAllCollision() ? PushReaction.IGNORE : PushReaction.NORMAL;
    }

    // Prevents collision with solid entities (shulkers, boats)
    @Override
    public boolean canCollideWith(Entity other) {
        return false;
    }

    /**
     * The camera is a client-only render helper, not a real living entity.  It
     * must never consume entity status events (for example status 35, the
     * Totem of Undying event), otherwise vanilla client events can mutate the
     * helper and make the camera flicker or briefly render entity effects.
     */
    @Override
    public void handleEntityEvent(byte status) {
    }

    // Ensures that the DetachedCamera is always in the swimming pose.
    @Override
    public void setPose(Pose pose) {
        super.setPose(Pose.SWIMMING);
    }

    // Prevents water submersion sounds from playing.
    @Override
    protected boolean updateIsUnderwater() {
        this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
        return this.wasUnderwater;
    }

    // Prevents water submersion sounds from playing.
    @Override
    protected void doWaterSplashEffect() {}

    private void doMotion() {
        switch (ModConfig.get().getFlightMode()) {
            case DEFAULT -> {
                getAbilities().setFlyingSpeed(0);
                Motion.doMotion(this, getHorizontalSpeed(), getVerticalSpeed());
            }
            case CREATIVE -> {
                getAbilities().setFlyingSpeed((float) getVerticalSpeed() / 10);

                if (this.input.keyPresses.shift() ^ this.input.keyPresses.jump()) {
                    int direction = this.input.keyPresses.jump() ? 1 : -1;
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0F, ((float) direction * this.getAbilities().getFlyingSpeed() * 3.0F), 0.0F));
                }
            }
        }
        getAbilities().flying = true;
        setOnGround(false);
    }

    /**
     * Applies the same additive wheel-step behavior used by vanilla spectator
     * flight while retaining BetterFC's configured horizontal/vertical ratio.
     */
    public void adjustSpeed(int scrollAmount) {
        if (scrollAmount == 0) {
            return;
        }

        scrollHorizontalSpeed = Mth.clamp(
                getHorizontalSpeed() + scrollAmount * SCROLL_SPEED_STEP,
                MIN_SCROLL_SPEED,
                MAX_SCROLL_SPEED
        );

        //~ if >=26.0 'displayClientMessage(Component.translatable("better_fc.msg.speed", scrollHorizontalSpeed), true)' -> 'sendOverlayMessage(Component.translatable("better_fc.msg.speed", scrollHorizontalSpeed))'
        MC.player.displayClientMessage(Component.translatable("better_fc.msg.speed", scrollHorizontalSpeed), true);
    }

    public double getHorizontalSpeed() {
        double speed = Double.isNaN(scrollHorizontalSpeed)
                ? ModConfig.get().getHorizontalSpeed()
                : scrollHorizontalSpeed;
        return Mth.clamp(speed, MIN_SCROLL_SPEED, MAX_SCROLL_SPEED);
    }

    public double getVerticalSpeed() {
        if (Double.isNaN(scrollHorizontalSpeed)) {
            return Mth.clamp(ModConfig.get().getVerticalSpeed(), MIN_SCROLL_SPEED, MAX_SCROLL_SPEED);
        }

        double configuredHorizontalSpeed = ModConfig.get().getHorizontalSpeed();
        double verticalRatio = configuredHorizontalSpeed > 0.0
                ? ModConfig.get().getVerticalSpeed() / configuredHorizontalSpeed
                : 1.0;
        return Mth.clamp(scrollHorizontalSpeed * verticalRatio, MIN_SCROLL_SPEED, MAX_SCROLL_SPEED);
    }

    @Override
    public float getViewXRot(float partialTick) {
        return this.getXRot();
    }

    @Override
    public float getViewYRot(float partialTick) {
        return this.getYRot();
    }

    // In newer versions, this also enables movement ticking (like below)
    @Override
    public boolean isEffectiveAi() {
        return true;
    }

    //? if >=1.21.11 {
    //In LivingEntity's aiStep(), this method decides whether to call travel(), enabling movement ticking
    @Override
    public boolean canSimulateMovement() {
        return true;
    }

    @Override
    protected void applyInput() {
        Vec2 vec2 = this.input.getMoveVector();
        if (vec2.lengthSquared() != 0.0F)
            vec2 = vec2.scale(0.98F);
        applyInputHelper(vec2, this.input.keyPresses.jump());
    }
    //? } else {
    /*@Override
    protected void serverAiStep() {
        Vec2 moveVector = new Vec2(this.input.keyPresses.left()Impulse, this.input.forwardImpulse);
        applyInputHelper(moveVector, this.input.keyPresses.jump());
    }
    *///? }

    private void applyInputHelper(Vec2 moveVector, boolean jumping) {
        this.xxa = moveVector.x;
        this.zza = moveVector.y;
        this.jumping = jumping;
        this.setSprinting((MC.options.keySprint.isDown() && this.input.keyPresses.forward()) || (this.input.keyPresses.forward() && this.isSprinting()));
        this.yBobO = this.yBob;
        this.xBobO = this.xBob;
        this.xBob = this.xBob + (this.getXRot() - this.xBob) * 0.5F;
        this.yBob = this.yBob + (this.getYRot() - this.yBob) * 0.5F;
    }
}

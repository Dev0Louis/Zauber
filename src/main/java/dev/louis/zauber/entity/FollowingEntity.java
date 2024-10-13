package dev.louis.zauber.entity;

import dev.louis.zauber.extension.EntityWithFollowingEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public abstract class FollowingEntity extends Entity implements Ownable {
    private static final double HARD_TELEPORT_SQUARED_DISTANCE = Math.pow(32, 2);
    private static final double PUSH_AWAY_SQUARED_DISTANCE = 1;
    private static final double MOVE_TO_PLAYER_SQUARED_DISTANCE = Math.pow(3, 2);
    private final double circleRotationSpeed;

    private final double heightOffset;
    private final ItemStack stack;

    private LivingEntity owner;

    public FollowingEntity(EntityType<?> type, World world) {
        this(type, world, ItemStack.EMPTY);
    }

    public FollowingEntity(EntityType<?> type, World world, ItemStack stack) {
        super(type, world);
        this.stack = stack;
        this.circleRotationSpeed = world.random.nextDouble() * 0.5 + 0.5;
        this.heightOffset = world.random.nextDouble() * 0.5;
        this.setVelocity(world.random.nextDouble() * 10, world.random.nextDouble() * 10, world.random.nextDouble() * 10);
    }

    @Override
    public void tick() {
        if (this.owner == null || this.owner.isRemoved() || !((EntityWithFollowingEntities) owner).zauber$getFollowingEntities().contains(this) || !this.isActive(owner)) {
            this.discard();
            return;
        }

        super.tick();

        var target = owner.getEyePos();
        double timer = 14f * circleRotationSpeed;
        double x = Math.sin(this.age / timer) * 2;
        double z = Math.cos(this.age / timer) * 2;
        target = target.add(x, .8 + heightOffset, z);
        var sqrtDistance = target.squaredDistanceTo(this.getPos());

        if (sqrtDistance > HARD_TELEPORT_SQUARED_DISTANCE) {
            this.setPosition(target);
            return;
        }

        var vec = this.getPos().relativize(target).normalize();

        if (sqrtDistance < PUSH_AWAY_SQUARED_DISTANCE) {
            //drag
            this.setVelocity(this.getVelocity().multiply(0.9));

            this.addVelocity(vec.multiply(-0.3));
        } else if (sqrtDistance > MOVE_TO_PLAYER_SQUARED_DISTANCE) {
            //drag
            this.setVelocity(this.getVelocity().multiply(0.9));

            this.addVelocity(vec.multiply(0.05));
        }

        this.velocityDirty = true;
        this.move(MovementType.SELF, this.getVelocity());
        //Debugger.addEntityBoundBox(this, this.getBoundingBox(), Color.BLACK);
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    public LivingEntity getOwner() {
        return owner;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public abstract boolean isActive(LivingEntity livingEntity);

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public void onActivation() {

    }
}

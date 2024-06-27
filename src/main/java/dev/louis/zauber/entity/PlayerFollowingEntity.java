package dev.louis.zauber.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class PlayerFollowingEntity extends Entity implements PolymerEntity {
    public static final EntityType<PlayerFollowingEntity> TYPE = FabricEntityTypeBuilder
            .<PlayerFollowingEntity>create(SpawnGroup.MISC, PlayerFollowingEntity::new)
            .build();
    private static final double HARD_TELEPORT_SQUARED_DISTANCE = Math.pow(12, 2);
    private static final double STAND_STILL_SQUARED_DISTANCE = Math.pow(2.5, 2);
    private static final double MOVE_TO_PLAYER_SQUARED_DISTANCE = Math.pow(3, 2);

    private final PlayerEntity player;

    public PlayerFollowingEntity(EntityType<?> type, World world) {
        this(type, world, world.getPlayers().get(0));
    }

    public PlayerFollowingEntity(EntityType<?> type, World world, PlayerEntity player) {
        super(type, world);
        this.player = player;
    }

    @Override
    public void tick() {
        super.tick();
        var target = player.getEyePos();
        double x = Math.sin(this.age / 15f) * 1.5;
        double z = Math.cos(this.age / 15f) * 1.5;
        target = target.add(x, 0, z);
        var vec = this.getPos().relativize(target);
        var sqrtDistance = target.squaredDistanceTo(this.getPos());;

        if (sqrtDistance > HARD_TELEPORT_SQUARED_DISTANCE) {
            this.setPosition(target);
            return;
        }

        if (sqrtDistance < STAND_STILL_SQUARED_DISTANCE && false) {
            this.setVelocity(this.getVelocity().multiply(0.9));
        } else {
            this.addVelocity(vec.normalize().multiply(0.1));
        }

        this.velocityDirty = true;
        this.move(MovementType.SELF, this.getVelocity());
        //Debugger.addEntityBoundBox(this, this.getBoundingBox(), Color.BLACK);
    }


    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    @Override
    protected void initDataTracker() {

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

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.SHULKER_BULLET;
    }
}

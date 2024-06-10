package dev.louis.zauber.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlayerFollowingEntity extends Entity implements PolymerEntity {
    /*public static final EntityType<PlayerFollowingEntity> TYPE = FabricEntityTypeBuilder
            .<PlayerFollowingEntity>create(SpawnGroup.MISC, PlayerFollowingEntity::new)
            .build();*/
    private static final double HARD_TELEPORT_SQUARED_DISTANCE = Math.pow(12, 2);
    private static final double NO_FOLLOW_SQUARED_DISTANCE = Math.pow(2.5, 2);
    private static final double CLOSEST_SQUARED_DISTANCE = Math.pow(2, 2);

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
        var vec = target.relativize(this.getPos());
        var sqrtDistance = target.squaredDistanceTo(this.getPos());

        Vec3d endPos;
        if (sqrtDistance > HARD_TELEPORT_SQUARED_DISTANCE) {
            endPos = target.add(vec.normalize().multiply(2));
        } else if (sqrtDistance < CLOSEST_SQUARED_DISTANCE) {
            endPos = target.add(vec.multiply(1.1f));
        } else if (sqrtDistance < NO_FOLLOW_SQUARED_DISTANCE){
            endPos = this.getPos();
        }else {
            endPos = target.add(vec.multiply(0.9f));
        }

        this.setPosition(endPos);
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
        return EntityType.HORSE;
    }
}

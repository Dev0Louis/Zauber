package dev.louis.zauber.entity;

import dev.louis.zauber.duck.AttachableEntity;
import dev.louis.zauber.item.ZauberItems;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class FollowingEntity extends Entity implements PolymerEntity {
    public static final EntityType<FollowingEntity> TYPE = FabricEntityTypeBuilder
            .<FollowingEntity>create(SpawnGroup.MISC, FollowingEntity::new)
            .build();
    private static final double HARD_TELEPORT_SQUARED_DISTANCE = Math.pow(32, 2);
    private static final double PUSH_AWAY_SQUARED_DISTANCE = 1;
    private static final double MOVE_TO_PLAYER_SQUARED_DISTANCE = Math.pow(3, 2);
    private final double circleRotationSpeed;

    private final LivingEntity owner;
    private final double heightOffset;

    public FollowingEntity(EntityType<?> type, World world) {
        this(type, world, world.getPlayers().get(0));
    }

    public FollowingEntity(EntityType<?> type, World world, LivingEntity owner) {
        super(type, world);
        this.circleRotationSpeed = world.random.nextDouble() * 0.5 + 0.5;
        this.heightOffset = world.random.nextDouble() * 0.5;
        this.owner = owner;
        this.setVelocity(world.random.nextDouble() * 10, world.random.nextDouble() * 10, world.random.nextDouble() * 10);
    }

    @Override
    public void tick() {
        if (this.owner == null || this.owner.isRemoved() || ((AttachableEntity) owner).zauber$getTotemOfDarkness() != this) {
            this.discard();
            return;
        }

        super.tick();

        var target = owner.getEyePos();
        double timer = 14f * circleRotationSpeed;
        double x = Math.sin(this.age / timer) * 2;
        double z = Math.cos(this.age / timer) * 2;
        target = target.add(x, .8 + heightOffset, z);
        var sqrtDistance = target.squaredDistanceTo(this.getPos());;

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
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        data.add(new DataTracker.SerializedEntry<>(DisplayEntity.ItemDisplayEntity.ITEM.getId(), DisplayEntity.ItemDisplayEntity.ITEM.getType(), ZauberItems.TOTEM_OF_DARKNESS.getDefaultStack()));
        data.add(new DataTracker.SerializedEntry<>(DisplayEntity.SCALE.getId(), DisplayEntity.SCALE.getType(), new Vector3f(.2f)));
        data.add(new DataTracker.SerializedEntry<>(DisplayEntity.TELEPORT_DURATION.getId(), DisplayEntity.TELEPORT_DURATION.getType(), 5));
        data.add(new DataTracker.SerializedEntry<>(DisplayEntity.BILLBOARD.getId(), DisplayEntity.BILLBOARD.getType(), (byte) 3));
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.ITEM_DISPLAY;
    }
}

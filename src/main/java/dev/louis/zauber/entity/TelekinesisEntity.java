package dev.louis.zauber.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class TelekinesisEntity extends Entity {
    PlayerEntity owner;

    public TelekinesisEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        /*if (this.getWorld().isClient()) {
            Vec3d vec3d = this.getVelocity();
            double dX = this.getX() + vec3d.x;
            double dY = this.getY() + vec3d.y;
            double dZ = this.getZ() + vec3d.z;
            this.setPosition(dX, dY, dZ);
        }*/
    }
}

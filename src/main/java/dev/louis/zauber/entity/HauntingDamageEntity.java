package dev.louis.zauber.entity;

import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

public class HauntingDamageEntity extends PersistentProjectileEntity implements PolymerEntity {
    private static final int TICKS_TILL_IMPACT = 20 * 30;
    public static final EntityType<HauntingDamageEntity> TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, HauntingDamageEntity::new)
            .build();
    private static final ItemStack STACK = new ItemStack(Items.TRIDENT);
    private DamageSource damageSource;
    private float damageAmount;

    public HauntingDamageEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world, STACK);
    }


    @Override
    public void tick() {
        var owner = this.getOwner();
        if (owner == null || !owner.isAlive()) {
            this.discard();
            return;
        }

        var target = owner.getPos().add(0, owner.getHeight(), 0).add(this.getOffset());
        var vec = target.subtract(this.getPos()).multiply(0.2);
        //if (vec.lengthSquared() < 0.8) vec.normalize();
        this.setVelocity(vec);


        super.tick();
        Vec3d vec3d = this.getVelocity();
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            double d = vec3d.horizontalLength();
            this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
            this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 180.0F / (float)Math.PI));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }

        BlockPos blockPos = this.getBlockPos();
        BlockState blockState = this.getWorld().getBlockState(blockPos);

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.isTouchingWaterOrRain() || blockState.isOf(Blocks.POWDER_SNOW)) {
            this.extinguish();
        }

        this.inGroundTime = 0;
        Vec3d vec3d3 = this.getPos();
        Vec3d vec3d2 = vec3d3.add(vec3d);
        HitResult hitResult = this.getWorld()
                .raycast(new RaycastContext(vec3d3, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d2 = hitResult.getPos();
        }

        while(!this.isRemoved()) {
            EntityHitResult entityHitResult = this.getEntityCollision(vec3d3, vec3d2);
            if (entityHitResult != null) {
                hitResult = entityHitResult;
            }

            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                Entity entity2 = this.getOwner();
                if (entity instanceof PlayerEntity && entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer((PlayerEntity)entity)) {
                    hitResult = null;
                    entityHitResult = null;
                }
            }

            if (entityHitResult != null) {
                //No block collision, but entity is okay :)
                this.onCollision(entityHitResult);
                this.velocityDirty = true;
            }

            if (entityHitResult == null || this.getPierceLevel() <= 0) {
                break;
            }

            hitResult = null;
        }

        vec3d = this.getVelocity();
        double e = vec3d.x;
        double f = vec3d.y;
        double g = vec3d.z;
        if (this.isCritical()) {
            for(int i = 0; i < 4; ++i) {
                this.getWorld()
                        .addParticle(
                                ParticleTypes.CRIT, this.getX() + e * (double)i / 4.0, this.getY() + f * (double)i / 4.0, this.getZ() + g * (double)i / 4.0, -e, -f + 0.2, -g
                        );
            }
        }

        double h = this.getX() + e;
        double j = this.getY() + f;
        double k = this.getZ() + g;
        double l = vec3d.horizontalLength();
        this.setYaw((float) (MathHelper.atan2(e, g) * 180.0F / (float) Math.PI));

        this.setPitch((float)(MathHelper.atan2(f, l) * 180.0F / (float)Math.PI));
        this.setPitch(updateRotation(this.prevPitch, this.getPitch()));
        this.setYaw(updateRotation(this.prevYaw, this.getYaw()));
        float m = 0.99F;
        if (this.isTouchingWater()) {
            for(int o = 0; o < 4; ++o) {
                this.getWorld().addParticle(ParticleTypes.BUBBLE, h - e * 0.25, j - f * 0.25, k - g * 0.25, e, f, g);
            }

            m = this.getDragInWater();
        }

        this.setVelocity(vec3d.multiply(m));

        this.setPosition(h, j, k);
        this.checkBlockCollision();
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return false;
    }

    public Vec3d getOffset() {
        var owner = this.getOwner();
        if (age < TICKS_TILL_IMPACT && owner instanceof PlayerEntity player && player.getSpellManager().isSpellTypeActive(Zauber.Spells.REFUSAL_OF_DEATH)) {
            var i = age / 10f;
            return new Vec3d(Math.sin(i) * 10, (Math.cos(i / 5f) + Math.sin(i / 5f)) / 2, Math.cos(i) * 10);
        } else {
            return Vec3d.ZERO;
        }
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.TRIDENT;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return List.of();
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {

    }

    public void setAttack(DamageSource damageSource, float damageAmount) {
        this.damageSource = damageSource;
        this.damageAmount = damageAmount;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity && entity.equals(this.getOwner())) {
            livingEntity.timeUntilRegen = 0;
            livingEntity.damage(damageSource, damageAmount);
            this.discard();
        }
    }
}

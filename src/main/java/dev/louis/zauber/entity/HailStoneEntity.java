package dev.louis.zauber.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Matrix4f;

public class HailStoneEntity extends Entity implements PolymerEntity {
    public static final EntityType<HailStoneEntity> TYPE = FabricEntityTypeBuilder
            .create(SpawnGroup.MISC, HailStoneEntity::new)
            .build();
    private float fallAcceleration;
    private final int damage = 3;

    public HailStoneEntity(EntityType<?> type, World world) {
        super(type, world);
        fallAcceleration = .1f * Math.max(world.getRandom().nextFloat(), 0.1f);
        EntityAttachment.ofTicking(new HailStoneEntity.CustomHolder(world), this);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void tick() {
        super.tick();
        if(getWorld().isClient())return;
        super.attemptTickInVoid();
        if(fallAcceleration < .1f) {
            fallAcceleration += .01f;
        }
        this.addVelocity(0, -fallAcceleration, 0);
        this.move(MovementType.SELF, this.getVelocity());
    }


    static float f = -2f;
    @Override
    public void onLanding() {
        if (this.getWorld().isClient()) return;
        if (!this.isOnGround()) return;
        this.playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.03f, 1.1f);
        ((ServerWorld)this.getWorld()).spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ICE.getDefaultState()),
                this.getX(),
                this.getY(),
                this.getZ(),
                10,
                0.1,
                0.1,
                0.1,
                0.01
        );

        this.getWorld().getPlayers(TargetPredicate.DEFAULT ,null, this.getBoundingBox().expand(2)).forEach(this::damagePlayer);
        this.discard();
    }


    private void damagePlayer(PlayerEntity player) {
        var damageSource = player.getDamageSources().freeze();
        double speedLength = this.getVelocity().length();
        int damage = MathHelper.ceil(MathHelper.clamp(speedLength * this.damage, 2.0, 100));

        if(player.getWorld().getBiome(player.getBlockPos()).value().isCold(player.getBlockPos())) {
            damage = damage * 2;
        }

        player.damage(damageSource, damage);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public boolean sendPacketsTo(ServerPlayerEntity player) {
        return false;
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return TYPE;
    }

    @SuppressWarnings("UnreachableCode")
    public static class CustomHolder extends ElementHolder {

        public CustomHolder(World world) {
            BlockDisplayElement blockDisplayElement = new BlockDisplayElement(Blocks.ICE.getDefaultState());
            blockDisplayElement.setTransformation(generateMatrix(world));
            this.addElement(blockDisplayElement);
        }

        protected static Matrix4f generateMatrix(World world) {
            Matrix4f transformationMatrix = new Matrix4f();
            transformationMatrix.rotateLocalX((float) world.random.nextTriangular(Math.PI, Math.PI));
            transformationMatrix.rotateLocalY((float) world.random.nextTriangular(Math.PI, Math.PI));
            transformationMatrix.rotateLocalZ((float) world.random.nextTriangular(Math.PI, Math.PI));
            transformationMatrix.scale(0.3456789f);
            return transformationMatrix;
        }


        @Override
        protected void onTick() {

        }
    }
}

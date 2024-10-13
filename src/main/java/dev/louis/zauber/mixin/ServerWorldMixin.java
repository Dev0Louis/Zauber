package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.extension.EntityExtension;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static dev.louis.zauber.block.SpellTableBlock.CHARGE;
import static dev.louis.zauber.block.SpellTableBlock.MAX_CHARGE;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LightningEntity;setCosmetic(Z)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onNaturalLightningImpact(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, Profiler profiler, BlockPos blockPos, LocalDifficulty localDifficulty, boolean bl2, LightningEntity lightningEntity) {
        Vec3i vec3i = new Vec3i(1, 1, 1);
        BlockPos blockPos1 = blockPos.add(0, -1, 0);


        Box box = new Box(blockPos1.subtract(vec3i).toCenterPos(), blockPos1.add(vec3i).toCenterPos());
        AtomicReference<Collection<BlockPos>> spellTableList = new AtomicReference<>(new ArrayList<>());
        final World world = chunk.getWorld();
        BlockPos.stream(box).forEach(pos -> {
            boolean isSpellTable = world.getBlockState(pos).getBlock() == ZauberBlocks.SPELL_TABLE;
            if (isSpellTable) {
                spellTableList.get().add(pos.toImmutable());
            }
        });
        final int size = spellTableList.get().size();
        spellTableList.get().forEach(pos -> {
            BlockState blockState = world.getBlockState(pos);
            int charge = Math.min(blockState.get(CHARGE) + (world.getRandom().nextBetween(9, 21) / size), MAX_CHARGE);
            blockState = blockState.with(CHARGE, charge);
            world.setBlockState(pos, blockState);
        });
        //new Box(blockPos.getX() - 3.0, blockPos.getY() - 3.0, blockPos.getZ() - 3.0, this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0)
    }

    @ModifyArg(
            method = "wakeSleepingPlayers",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;")
    )
    public Predicate<PlayerEntity> playerEntityPredicate(Predicate<PlayerEntity> predicate) {
        return predicate.and(Zauber::isNotInTrappingBed);
    }

    /*@WrapOperation(
            method = "tickEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V")
    )
    public void teleknieseEntities(Entity entity, Operation<Void> original) {
        ((EntityExtension) entity).getTelekineser().ifPresentOrElse(telekineser -> {
            var prevX = entity.getX();
            var prevY = entity.getY();
            var prevZ = entity.getZ();
            original.call(entity);

            entity.setPosition(prevX, prevY, prevZ);

            var target = telekineser.getEyePos().add(telekineser.getRotationVector().normalize().multiply(6).add(0, -.5, 0));
            entity.setVelocity(entity.getVelocity().multiply(0.75));
            var vel = target.subtract(entity.getPos()).multiply(0.1);
            entity.addVelocity(vel);
            entity.velocityModified = true;
            entity.move(MovementType.SELF, entity.getVelocity());
        }, () -> original.call(entity));
    }*/
}

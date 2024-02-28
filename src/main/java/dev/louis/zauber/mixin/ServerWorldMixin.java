package dev.louis.zauber.mixin;

import dev.louis.zauber.block.ZauberBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static dev.louis.zauber.block.SpellTableBlock.CHARGE;
import static dev.louis.zauber.block.SpellTableBlock.MAX_CHARGE;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LightningEntity;setCosmetic(Z)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onNaturalLightningImpact(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, Profiler profiler, BlockPos blockPos, LocalDifficulty localDifficulty, boolean bl2, LightningEntity lightningEntity) {
        Vec3i vec3i = new Vec3i(1, 1, 1);
        BlockPos blockPos1 = blockPos.add(0, -1, 0);


        Box box = new Box(blockPos1.subtract(vec3i).toCenterPos(), blockPos1.add(vec3i).toCenterPos());
        AtomicReference<Collection<BlockPos>> spellTableList = new AtomicReference<>(new ArrayList<>());
        final World world = chunk.getWorld();
        BlockPos.stream(box).forEach(pos -> {
            boolean isSpellTable = world.getBlockState(pos).getBlock() == ZauberBlocks.SPELL_TABLE;
            if(isSpellTable) {
                spellTableList.get().add(pos.toImmutable());
            }
        });
        final int size = spellTableList.get().size();
        spellTableList.get().forEach(pos -> {
            BlockState blockState = world.getBlockState(pos);
            int charge = Math.min(blockState.get(CHARGE)+(world.getRandom().nextBetween(9, 21)/size), MAX_CHARGE);
            blockState = blockState.with(CHARGE, charge);
            world.setBlockState(pos, blockState);
        });
        //new Box(blockPos.getX() - 3.0, blockPos.getY() - 3.0, blockPos.getZ() - 3.0, this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0)
    }
}

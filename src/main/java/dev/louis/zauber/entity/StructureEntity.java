package dev.louis.zauber.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public abstract class StructureEntity extends Entity implements PolymerEntity {

    public StructureEntity(EntityType<?> type, World world) {
        super(type, world);
        EntityAttachment.ofTicking(new CustomHolder(this), this);
    }

    public abstract float getSize();

    public abstract BlockState[][][] getBlockStateArray();

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
        return this.getType();
    }

    @Override
    public boolean sendPacketsTo(ServerPlayerEntity player) {
        return false;
    }

    @SuppressWarnings("UnreachableCode")
    public static class CustomHolder extends ElementHolder {
        private final StructureEntity structureEntity;
        private final Vec3i structureSize;
        private final Map<BlockDisplayElement, BlockPos> blockDisplays = new HashMap<>();
        private float lastSize;

        public CustomHolder(StructureEntity structureEntity) {
            BlockState[][][] blockArray = structureEntity.getBlockStateArray();
            this.structureEntity = structureEntity;
            this.structureSize = new Vec3i(blockArray.length, blockArray[0].length, blockArray[0][0].length);
            for (int x = 0; x < this.structureSize.getX(); x++) {
                for (int y = 0; y < this.structureSize.getY(); y++) {
                    for (int z = 0; z < this.structureSize.getZ(); z++) {
                        BlockState blockState = blockArray[x][y][z];
                        if (blockState == null) continue;
                        BlockDisplayElement blockDisplay = new BlockDisplayElement(blockState);
                        blockDisplay.setInterpolationDuration(10);

                        this.blockDisplays.put(blockDisplay, new BlockPos(x, y, z));
                        this.addElement(blockDisplay);
                    }
                }
            }
            this.updateOffset();
        }

        private void updateOffset() {
            blockDisplays.forEach((blockDisplay, pos) -> {
                float size = this.structureEntity.getSize();
                blockDisplay.setOffset(
                        //We center the structurePos
                        Vec3d.of(pos)
                                .multiply(size)
                                .subtract(structureSize.getX() / 2f * size, 0, structureSize.getZ() / 2f * size)
                );
                blockDisplay.setScale(new Vector3f(size));
            });
        }

        @Override
        protected void onTick() {
            if (this.lastSize == this.structureEntity.getSize())return;
            this.lastSize = this.structureEntity.getSize();
            this.updateOffset();
        }
    }
}

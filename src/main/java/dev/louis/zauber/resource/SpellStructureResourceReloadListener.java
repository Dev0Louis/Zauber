package dev.louis.zauber.resource;

import dev.louis.zauber.entity.IcePeakEntity;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SpellStructureResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    public static Logger LOGGER = LoggerFactory.getLogger(SpellStructureResourceReloadListener.class);

    @Override
    public void reload(ResourceManager manager) {
        StructureTemplate iceSpikeStructure = new StructureTemplate();
        //Should be there 100%
        var resource = manager.getResource(Identifier.of("zauber", "spell_structures/ice_spike.nbt"));
        try {
            iceSpikeStructure.readNbt(
                    Registries.BLOCK.getReadOnlyWrapper(), NbtIo.readCompressed(resource.orElseThrow().getInputStream(), NbtSizeTracker.ofUnlimitedBytes())
            );
            IcePeakEntity.ICE_SPIKE_ARRAY = this.generateArray(iceSpikeStructure, true);
        } catch (IOException e) {
            LOGGER.error("Failed to load ice spike structure, will load placeholder structure instead.");
            IcePeakEntity.ICE_SPIKE_ARRAY = IcePeakEntity.createEmptyArray();
        }
    }


    private BlockState[][][] generateArray(StructureTemplate structureTemplate, boolean optimize) {
        // A 3-Dimensional Array which is true at places where the block is occupied (can't be looked through)
        int sizeX = structureTemplate.getSize().getX();
        int sizeY = structureTemplate.getSize().getY();
        int sizeZ = structureTemplate.getSize().getZ();
        // This array shall not be modified it is used to check the BlockState at the given Position.
        BlockState[][][] startArray = generate3DArray(structureTemplate);

        if (!optimize) {
            return startArray;
        }

        // This array can be modified and will be returned in the end.
        BlockState[][][] writeArray = new BlockState[sizeX][sizeY][sizeZ];
        //Here we loop over the entire array
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {

                    BlockState state = startArray[x][y][z];
                    if (state == null || state.isAir()) continue;
                    boolean shouldWrite = false;

                    //This is to check all offsets
                    offset:
                    for (int xOffset = -1; xOffset <= 1; xOffset++) {
                        for (int yOffset = -1; yOffset <= 1; yOffset++) {
                            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                                int i = Math.abs(xOffset) + Math.abs(yOffset) + Math.abs(zOffset);
                                if (i == 0 || i >= 2) continue;

                                int x1 = x + xOffset;
                                int y1 = y + yOffset;
                                int z1 = z + zOffset;
                                if (!(x1 < 0 || y1 < 0 || z1 < 0 || x1 >= sizeX || y1 >= sizeY || z1 >= sizeZ)) {
                                    BlockState blockState = startArray[x1][y1][z1];
                                    //As soon was we detect anywhere where there is
                                    //System.out.println(blockState + " canBeSeenThrough " + canBeSeenThrough(blockState));
                                    if (blockState != null && !canBeSeenThrough(blockState)) {
                                        continue;
                                    }
                                }
                                shouldWrite = true;
                                break offset;
                            }
                        }
                    }

                    if (shouldWrite) {
                        writeArray[x][y][z] = state;
                    }
                }
            }
        }
        return writeArray;
    }

    public boolean canBeSeenThrough(BlockState blockState) {
        var block = blockState.getBlock();
        return blockState.isAir() || !blockState.isSolid() || !blockState.isOpaque() || block instanceof SlabBlock || block instanceof StairsBlock || block instanceof BlockWithEntity;
    }


    private static BlockState[][][] generate3DArray(StructureTemplate structureTemplate) {
        int sizeX = structureTemplate.getSize().getX();
        int sizeY = structureTemplate.getSize().getY();
        int sizeZ = structureTemplate.getSize().getZ();
        BlockState[][][] blockArray = new BlockState[sizeX][sizeY][sizeZ];
        structureTemplate.blockInfoLists.forEach(palettedBlockInfoList -> {
            palettedBlockInfoList.getAll().forEach(structureBlockInfo -> {
                BlockPos blockPos = structureBlockInfo.pos();
                blockArray[blockPos.getX()][blockPos.getY()][blockPos.getZ()] = structureBlockInfo.state();
            });
        });
        return blockArray;
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of("zauber", "spell_structures");
    }
}

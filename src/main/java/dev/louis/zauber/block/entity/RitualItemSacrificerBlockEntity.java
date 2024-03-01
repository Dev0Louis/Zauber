package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.ZauberBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class RitualItemSacrificerBlockEntity extends BlockEntity {
    public static final BlockEntityType<RitualItemSacrificerBlockEntity> TYPE = BlockEntityType.Builder.create(RitualItemSacrificerBlockEntity::new, ZauberBlocks.RITUAL_ITEM_SACRIFICER).build(null);


    public RitualItemSacrificerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, RitualItemSacrificerBlockEntity ritualStoneBlockEntity) {

    }

    public void informRitualBlocks(Consumer<RitualStoneBlockEntity> informer) {
        Box box = Box.of(this.pos.toCenterPos(), 10, 10, 10);
        BlockPos.stream(box).forEach(blockPos -> {
            world.getBlockEntity(blockPos, RitualStoneBlockEntity.TYPE).ifPresent(informer);
        });
    }
}

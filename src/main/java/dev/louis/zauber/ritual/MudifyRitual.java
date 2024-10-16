package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class MudifyRitual extends Ritual {
    private int transformableBlocks;
    private boolean ranOutOfMana = false;

    public Collection<BlockPos.Mutable> mudders = new ArrayList<>();


    public MudifyRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        super(world, ritualStoneBlockEntity);
    }

    @Override
    public void tick() {
        int manaDue = 0;
        //for loop to access int, that would in forEach be out of scope.
        for (BlockPos.Mutable mutablePos : mudders) {
            if (transformableBlocks > 0) {
                var direction = Direction.random(world.random);
                if (world.getBlockState(mutablePos.move(direction)).isIn(BlockTags.DIRT)) {
                    if (world.getBlockState(mutablePos).isOf(Blocks.MUD)) break;

                    world.setBlockState(mutablePos, Blocks.MUD.getDefaultState());
                    world.playSound(null, mutablePos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, -1.0F);
                    world.emitGameEvent(null, GameEvent.FLUID_PLACE, mutablePos);

                    /*SoundHelper.playSound(
                            (ServerWorld) world,
                            mutablePos,
                            SoundEvents.BLOCK_MUD_PLACE,
                            SoundCategory.BLOCKS,
                            1,
                            1
                    );*/
                    if (transformableBlocks % 15 == 0) manaDue++;
                    transformableBlocks--;
                } else {
                    mutablePos.move(direction.getOpposite());
                }
            }
        }

        if (manaDue <= 0) return;

        final int finalManaDue = manaDue;
        for (int i = 0; i < finalManaDue; i++) {
            var optional = ritualStoneBlockEntity.acquireManaReference();
            if (optional.isPresent()) {
                manaDue--;
                optional.get().apply();
            }
        }
        if (manaDue != 0) this.ranOutOfMana = true;
        //Get mana and if not get able end.
    }

    @Override
    public void onStart() {
        if (ritualStoneBlockEntity.getStoredStack().isOf(Items.WATER_BUCKET)) {
            this.transformableBlocks += 30;
            ritualStoneBlockEntity.setStoredStack(Items.BUCKET.getDefaultStack());
        }
        var waterBuckets = this.ritualStoneBlockEntity.getNonEmptyItemSacrificers().filter(itemSacrificerBlockEntity -> itemSacrificerBlockEntity.getStoredStack().isOf(Items.WATER_BUCKET)).toList();
        this.transformableBlocks += 30 * waterBuckets.size();
        waterBuckets.stream().limit(30).forEach(itemSacrificerBlockEntity -> {
            this.mudders.add(this.pos.mutableCopy());
            itemSacrificerBlockEntity.setStoredStack(Items.BUCKET.getDefaultStack());
        });

        this.mudders.add(this.pos.mutableCopy());
        this.mudders.add(this.pos.mutableCopy());
        this.mudders.add(this.pos.mutableCopy());
    }

    @Override
    public void finish() {

    }

    @Override
    public boolean shouldStop() {
        return transformableBlocks <= 0 || age > 1000 || ranOutOfMana;
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        var ritualItemStack = ritualStoneBlockEntity.getStoredStack();

        var manaCauldrons = ritualStoneBlockEntity.getManaStoragesStream().collect(Collectors.toList());
        if (manaCauldrons.isEmpty() || !ritualItemStack.isOf(Items.WATER_BUCKET)) return null;
        return new MudifyRitual(world, ritualStoneBlockEntity);
    }
}

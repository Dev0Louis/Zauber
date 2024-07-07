package dev.louis.zauber.ritual;

import dev.louis.zauber.block.ManaCauldron;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.item.ZauberItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ManaBowRitual extends Ritual {
    private final BlockPos manaStorageBlockPos;

    protected ManaBowRitual(World world, RitualStoneBlockEntity blockEntity, BlockPos manaStorageBlockPos) {
        super(world, blockEntity);
        this.manaStorageBlockPos = manaStorageBlockPos;
    }

    @Override
    public void tick() {
        if(age % 5 == 0) {
            world.playSound(null, this.pos, SoundEvents.ENTITY_ARROW_HIT, SoundCategory.PLAYERS, 1, -4);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public boolean shouldStop() {
        return age > 40;
    }

    @Override
    public void finish() {
        //ItemSacrificerBlockEntity with a call goat horn.
        var manaStorageState = world.getBlockState(manaStorageBlockPos);
        int manaLevel = manaStorageState.get(ManaCauldron.MANA_LEVEL);
        if (manaLevel == 2 && ManaBowRitual.isBow(ritualStoneBlockEntity.getStoredStack())) {
            ritualStoneBlockEntity.setStoredStack(ItemStack.EMPTY);
            world.setBlockState(manaStorageBlockPos, Blocks.CAULDRON.getDefaultState());
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, ZauberItems.MANA_BOW.getDefaultStack(), 0, 0.3f, 0));
        }
    }

    @Override
    public float getVolume() {
        return 2;
    }

    @Override
    public Stream<Position> getConnections() {
        return Stream.of(
                this.manaStorageBlockPos.toCenterPos()
        );
    }

    @Override
    public float getPitch() {
        return -2;
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        var ritualItemStack = ritualStoneBlockEntity.getStoredStack();

        var filledManaStorage = ritualStoneBlockEntity.getFilledManaStorages().findAny();
        if(!ManaBowRitual.isBow(ritualItemStack) || filledManaStorage.isEmpty()) return null;
        return new ManaBowRitual(world, ritualStoneBlockEntity, filledManaStorage.get());
    }



    public static boolean isBow(@Nullable ItemStack itemStack) {
        return itemStack.isOf(Items.BOW);
    }
}

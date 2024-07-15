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

import java.util.List;
import java.util.stream.Stream;

public class TotemOfManaRitual extends Ritual implements ManaPullingRitual {

    int mana;

    protected TotemOfManaRitual(World world, RitualStoneBlockEntity blockEntity) {
        super(world, blockEntity);
    }

    @Override
    public void tick() {
        if(age % 5 == 0) {
            world.playSound(null, this.pos, SoundEvents.ENTITY_ARROW_HIT, SoundCategory.PLAYERS, 1, -4);
        }
        if (age % 8 == 0) {
            ritualStoneBlockEntity.getFilledManaStorages().findAny().ifPresent(blockPos -> {
                var state = world.getBlockState(blockPos);
                int newManaLevel = state.get(ManaCauldron.MANA_LEVEL) - 1;
                if (newManaLevel >= 1) {
                    world.setBlockState(blockPos, state.with(ManaCauldron.MANA_LEVEL, newManaLevel));
                } else {
                    world.setBlockState(blockPos, Blocks.CAULDRON.getDefaultState());
                }
                mana++;
            });
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public boolean shouldStop() {
        return age > 80;
    }

    @Override
    public void finish() {
        if (mana >= 8 && isTotem(ritualStoneBlockEntity.getStoredStack())) {
            ritualStoneBlockEntity.setStoredStack(ItemStack.EMPTY);
            //I REALLY REALLY REALLY NEED A SYSTEM TO ABSTRACT OVER THE MANUALLY SETTING AHHHHHHHHHHHHHh
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, ZauberItems.TOTEM_OF_MANA.getDefaultStack(), 0, 0.3f, 0));
            world.playSound(null, this.pos, SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED, SoundCategory.PLAYERS, 2, -4);

        }
    }

    @Override
    public float getVolume() {
        return 2;
    }

    @Override
    public float getPitch() {
        return -2;
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        var ritualItemStack = ritualStoneBlockEntity.getStoredStack();

        boolean has4FullMana = ritualStoneBlockEntity.getFilledManaStorages().findAny().isPresent();
        if(!isTotem(ritualItemStack) || !has4FullMana) return null;
        return new TotemOfManaRitual(world, ritualStoneBlockEntity);
    }

    public static boolean isTotem(ItemStack itemStack) {
        return itemStack.isOf(Items.TOTEM_OF_UNDYING);
    }


}

package dev.louis.zauber.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;

public class EffectHelper {
    public static void playBreakItemEffect(ServerWorld world, BlockPos pos, ItemStack itemStack) {
        playBreakItemEffect(world, pos.toCenterPos(), itemStack);
    }


    public static void playBreakItemEffect(ServerWorld world, Position pos, ItemStack itemStack) {
        ParticleHelper.spawn50Particles(
                world,
                pos,
                new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack)
        );
        SoundHelper.playSound(
                world,
                pos,
                SoundEvents.ITEM_SHIELD_BREAK,
                SoundCategory.AMBIENT,
                0.6f,
                0.9f
        );
    }

    public static void playBloodItemEffect(ServerWorld world, BlockPos pos, ItemStack itemStack) {
        playBloodItemEffect(world, pos.toCenterPos(), itemStack);
    }

    public static void playBloodItemEffect(ServerWorld world, Position pos, ItemStack itemStack) {
        ParticleHelper.spawn50Particles(
                world,
                pos,
                new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack)
        );
        SoundHelper.playSound(
                world,
                pos,
                SoundEvents.BLOCK_LAVA_POP,
                SoundCategory.AMBIENT,
                10.6f,
                2.9f
        );
    }
}

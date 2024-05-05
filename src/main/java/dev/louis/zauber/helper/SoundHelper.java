package dev.louis.zauber.helper;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;

public class SoundHelper {
    public static void playBlockSound(ServerWorld world, BlockPos pos, SoundEvent sound) {
        playSound(world, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public static void playSound(ServerWorld serverWorld, BlockPos pos, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        serverWorld.playSound(null, pos, sound, soundCategory, volume, pitch);
    }

    public static void playSound(ServerWorld serverWorld, Position pos, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        serverWorld.playSound(null, pos.getX(), pos.getY(), pos.getZ(), sound, soundCategory, volume, pitch);
    }
}

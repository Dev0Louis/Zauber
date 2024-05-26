package dev.louis.zauber.item;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.helper.SoundHelper;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;

public class HeartOfTheDarknessItem extends Item implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    public static int MAX_BRIGHTNESS = 8;
    public static Collection<Block> SIMPLE_DESTROYABLE = List.of(Blocks.SEA_LANTERN, Blocks.GLOWSTONE, Blocks.LANTERN, Blocks.SHROOMLIGHT);

    public HeartOfTheDarknessItem(Settings settings) {
        super(settings);
    }

    public static void onDisappeared(ServerWorld world, Vec3d pos) {
        SoundHelper.playSound(
                world,
                pos,
                SoundEvents.ENTITY_SHULKER_BULLET_HIT,
                SoundCategory.AMBIENT,
                1,
                1
        );
        ParticleHelper.spawn50Particles(
                world,
                pos,
                new DustParticleEffect(new Vector3f(), 1f)
        );
        var entityBox = Box.of(pos, 6, 6, 6);
        world.getEntitiesByClass(LivingEntity.class, entityBox, EntityPredicates.EXCEPT_SPECTATOR).forEach(entity -> {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 0));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 0));
        });
        var exinguishingBox = Box.of(pos, 12, 12, 12);
        BlockPos.stream(exinguishingBox).forEach(blockPos -> {
            var blockState = world.getBlockState(blockPos);
            if (SIMPLE_DESTROYABLE.stream().anyMatch(blockState::isOf)) {
                world.breakBlock(blockPos, true);
                return;
            }
            if (blockState.getOrEmpty(Properties.LIT).orElse(false)) {
                world.setBlockState(blockPos, blockState.with(Properties.LIT, false));
            }
            if (blockState.isOf(Blocks.FIRE)) {
                world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
            }
            if (blockState.isOf(Blocks.JACK_O_LANTERN)) {
                world.setBlockState(blockPos, Blocks.CARVED_PUMPKIN.getDefaultState().with(HorizontalFacingBlock.FACING, blockState.get(HorizontalFacingBlock.FACING)));
            }
            if (blockState.isOf(Blocks.TORCH)) {
                world.setBlockState(blockPos, ZauberBlocks.EXTINGUISHED_TORCH.getDefaultState());
            }
            if (blockState.isOf(Blocks.WALL_TORCH)) {
                world.setBlockState(blockPos, ZauberBlocks.EXTINGUISHED_WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, blockState.get(WallTorchBlock.FACING)));
            }
        });
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if (Zauber.isClientModded(player)) return this;
        return Items.HEART_OF_THE_SEA.asItem();
    }
}

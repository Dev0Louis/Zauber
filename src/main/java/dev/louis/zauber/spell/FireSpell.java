package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FireSpell extends AreaEffectSpell {
    public FireSpell(SpellType<? extends AreaEffectSpell> spellType, PlayerEntity caster) {
        super(spellType, caster, ParticleTypes.FLAME);
    }

    @Override
    protected void affect(Entity entity) {
        entity.setVelocity(entity.getPos().subtract(getCaster().getPos()).normalize().add(0, 1, 0));
        entity.velocityModified = true;
        entity.setFireTicks(100);
        super.affect(entity);
    }

    @Override
    protected void affect(ServerWorld serverWorld, BlockPos blockPos) {
        var blockState = serverWorld.getBlockState(blockPos);
        if (blockState.isOf(Blocks.TNT)) {
            spawnTnt(serverWorld, blockPos, this.getCaster());
            serverWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
        }
        super.affect(serverWorld, blockPos);
    }

    private static void spawnTnt(World world, BlockPos pos, LivingEntity igniter) {
        if (!world.isClient()) {
            TntEntity tntEntity = new TntEntity(world, (double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5, igniter);
            world.spawnEntity(tntEntity);
            world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        }
    }

    @Override
    public int getDuration() {
        return ConfigManager.getServerConfig().fireSpellDuration();
    }
}

package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class IceSpell extends AreaEffectSpell {
    public IceSpell(SpellType<? extends AreaEffectSpell> spellType) {
        super(spellType, ParticleTypes.SNOWFLAKE);
    }

    @Override
    protected void affect(Entity entity) {
        entity.setVelocity(Vec3d.ZERO);
        entity.velocityModified = true;
        entity.setFrozenTicks(100);
        entity.extinguishWithSound();
        super.affect(entity);
    }

    @Override
    protected void affect(ServerWorld serverWorld, BlockPos blockPos) {
        var blockState = serverWorld.getFluidState(blockPos);
        if (blockState.isIn(FluidTags.WATER)) {
            serverWorld.setBlockState(blockPos, Blocks.FROSTED_ICE.getDefaultState(), Block.NOTIFY_ALL);
        }
        super.affect(serverWorld, blockPos);
    }

    @Override
    public int getDuration() {
        return ConfigManager.getServerConfig().iceSpellDuration();
    }
}

package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.config.ConfigManager;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.TeleportTarget;

public class RewindSpell extends Spell {
    private ServerWorld rewindWorld;
    private TeleportTarget rewindTarget;

    public RewindSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        rewindWorld = (ServerWorld) getCaster().getWorld();
        rewindTarget = new TeleportTarget(getCaster().getPos(), getCaster().getVelocity(), getCaster().getYaw(), getCaster().getPitch());
        Zauber.LOGGER.debug("Player setting rewindTarget to " + rewindTarget.position + " and rewindWorld to " + rewindWorld.getRegistryKey().getValue());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getCaster() instanceof ServerPlayerEntity serverPlayer) {
            if(age % 10 == 0) playPingSound(serverPlayer);
        }
    }

    @Override
    public void finish() {
        if(!this.wasInterrupted && this.getCaster() instanceof ServerPlayerEntity serverPlayer) {
            double x = rewindTarget.position.getX();
            double y = rewindTarget.position.getY();
            double z = rewindTarget.position.getY();

            this.playRewindSound(serverPlayer);
            rewindWorld.spawnParticles(
                    ParticleTypes.REVERSE_PORTAL,
                    x,
                    y,
                    z,
                    2,
                    0,
                    0,
                    0,
                    0
            );
            Zauber.LOGGER.debug("Player is rewinding to " + rewindTarget.position + " in " + rewindWorld.getRegistryKey().getValue());
            FabricDimensions.teleport(getCaster(), rewindWorld, rewindTarget);
        }
    }

    @Override
    public int getDuration() {
        return ConfigManager.getServerConfig().rewindSpellDuration();
    }

    private void playPingSound(ServerPlayerEntity player) {
        player.getServerWorld().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(),
                player.getSoundCategory(),
                1,
                -1
        );
    }

    private void playRewindSound(ServerPlayerEntity player) {
        player.getServerWorld().playSound(
                null,
                rewindTarget.position.getX(),
                rewindTarget.position.getY(),
                rewindTarget.position.getZ(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                player.getSoundCategory(),
                1,
                1
        );
    }
}

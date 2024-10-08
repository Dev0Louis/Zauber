package dev.louis.zauber.spell.effect;

import dev.louis.nebula.api.spell.SpellEffect;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.spell.effect.type.SpellEffectTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.TeleportTarget;

public class RewindSpellEffect extends SpellEffect {
    protected TeleportTarget rewindTarget;

    public RewindSpellEffect(LivingEntity target) {
        super(SpellEffectTypes.REWIND, target);
    }

    public RewindSpellEffect(LivingEntity target, TeleportTarget teleportTarget) {
        super(SpellEffectTypes.REWIND, target);
        setRewindTarget(teleportTarget);
    }


    @Override
    public void onStart() {

    }

    @Override
    public void tick() {
        if (age % 10 == 0) playPingSound(target);

    }

    @Override
    public void onEnd() {
        double x = rewindTarget.pos().getX();
        double y = rewindTarget.pos().getY();
        double z = rewindTarget.pos().getY();

        this.playRewindSound(target);
        rewindTarget.world().spawnParticles(
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
        Zauber.LOGGER.debug("Player is rewinding to " + rewindTarget.pos() + " in " + rewindWorld.getRegistryKey().getValue());
        target.teleportTo(rewindTarget);
    }

    public void setRewindTarget(TeleportTarget rewindTarget) {
        this.rewindTarget = rewindTarget;
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
                rewindTarget.pos().getX(),
                rewindTarget.pos().getY(),
                rewindTarget.pos().getZ(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                player.getSoundCategory(),
                1,
                1
        );
    }
}

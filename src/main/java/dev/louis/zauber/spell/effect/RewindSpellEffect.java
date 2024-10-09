package dev.louis.zauber.spell.effect;

import dev.louis.nebula.api.spell.SpellEffect;
import dev.louis.zauber.spell.effect.type.SpellEffectTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.TeleportTarget;

public class RewindSpellEffect extends SpellEffect {
    protected TeleportTarget rewindTarget;

    public RewindSpellEffect(LivingEntity target) {
        super(SpellEffectTypes.REWIND, target);
    }

    @Override
    public void onStart() {
        var rewindWorld = (ServerWorld) this.target.getWorld();
        this.rewindTarget = new TeleportTarget(rewindWorld, this.target.getPos(), this.target.getVelocity(), this.target.getYaw(), this.target.getPitch(), TeleportTarget.NO_OP);
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
        target.teleportTo(rewindTarget);
    }

    private void playPingSound(LivingEntity entity) {
        entity.playSound(
                SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(),
                1,
                -1
        );
    }

    private void playRewindSound(LivingEntity player) {
        player.playSound(
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                1,
                1
        );
    }
}

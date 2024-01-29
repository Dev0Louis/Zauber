package dev.louis.zauber.spell;

import dev.louis.zauber.accessor.DashingLivingEntity;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ZauberConfig;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class DashSpell extends Spell {
    public DashSpell(SpellType<?> spellType) {
        super(spellType);
    }

    @Override
    public void cast() {
        if(this.getCaster() instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.setInvisible(true);
            serverPlayer.setInvulnerable(true);
            ((DashingLivingEntity) serverPlayer).zauber$setDashing(true);
            serverPlayer.playSound(
                    SoundEvents.BLOCK_DEEPSLATE_TILES_HIT,
                    SoundCategory.PLAYERS,
                    2f,
                    -5f
            );
        }
    }

    @Override
    public void tick() {
        if(getCaster() instanceof ServerPlayerEntity serverPlayer) {
            if(serverPlayer.getVelocity().lengthSquared() < 0.5 && spellAge > 1) {
                stop();
            }

            serverPlayer.getServerWorld().getOtherEntities(serverPlayer, serverPlayer.getBoundingBox().expand(0.2)).forEach(entity -> {
                //entity.setVelocity(entity.getRotationVector().negate().add(0, 1, 0));
                entity.velocityModified = true;
            });

            serverPlayer.playSound(
                    SoundEvents.UI_STONECUTTER_TAKE_RESULT,
                    SoundCategory.PLAYERS,
                    2f,
                    -5f
            );


            serverPlayer.getServerWorld().spawnParticles(
                    serverPlayer,
                    ParticleTypes.END_ROD,
                    false,
                    serverPlayer.getX(),
                    serverPlayer.getY(),
                    serverPlayer.getZ(),
                    10,
                    0.5,
                    0.5,
                    0.5,
                    0.1
            );
            serverPlayer.setVelocity(serverPlayer.getRotationVector().multiply(1.5, 0.2, 1.5));
            serverPlayer.velocityModified = true;
            serverPlayer.velocityDirty = true;
        }
    }

    @Override
    public void onEnd() {
        this.getCaster().setInvisible(false);
        this.getCaster().setInvulnerable(false);
        ((DashingLivingEntity) this.getCaster()).zauber$setDashing(false);

    }

    @Override
    public int getDuration() {
        return ZauberConfig.getDashSpellDuration();
    }
}

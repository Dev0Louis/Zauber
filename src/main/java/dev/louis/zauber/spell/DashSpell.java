package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ConfigManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class DashSpell extends Spell {
    protected Vec3d dashVelocity;

    public DashSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        if(this.getCaster() instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.setInvisible(true);
            serverPlayer.setInvulnerable(true);
            serverPlayer.setNoDrag(true);
            var dashVelocityMultiplier = ConfigManager.getServerConfig().dashVelocityMultiplier();
            dashVelocity = serverPlayer.getRotationVector().multiply(dashVelocityMultiplier, 0.2, dashVelocityMultiplier);
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
            if(serverPlayer.getVelocity().lengthSquared() < 0.3 && age > 2) {
                stop();
                return;
            }

            serverPlayer.getServerWorld().getOtherEntities(serverPlayer, serverPlayer.getBoundingBox().expand(0.2)).forEach(entity -> {
                entity.setVelocity(entity.getRotationVector().negate().add(0, 0.75, 0));
                entity.velocityModified = true;
            });

            serverPlayer.playSound(
                    SoundEvents.UI_STONECUTTER_TAKE_RESULT,
                    SoundCategory.PLAYERS,
                    2f,
                    -5f
            );


            serverPlayer.getServerWorld().spawnParticles(
                    ParticleTypes.END_ROD,
                    serverPlayer.getX(),
                    serverPlayer.getY(),
                    serverPlayer.getZ(),
                    10,
                    0.5,
                    0.5,
                    0.5,
                    0.1
            );
            serverPlayer.setVelocity(dashVelocity);
            serverPlayer.velocityModified = true;
            serverPlayer.velocityDirty = true;
        }
    }

    @Override
    public void finish() {
        this.getCaster().setInvisible(false);
        this.getCaster().setInvulnerable(false);
        this.getCaster().setNoDrag(false);
    }

    @Override
    public int getDuration() {
        return ConfigManager.getServerConfig().dashSpellDuration();
    }
}

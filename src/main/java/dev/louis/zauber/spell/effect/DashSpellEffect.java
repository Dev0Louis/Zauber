package dev.louis.zauber.spell.effect;

import dev.louis.nebula.api.spell.SpellEffect;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.spell.effect.type.SpellEffectTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class DashSpellEffect extends SpellEffect {
    private Vec3d dashVelocity;
    private boolean aborted;


    public DashSpellEffect(LivingEntity target) {
        super(SpellEffectTypes.DASH, target);
    }

    @Override
    public void onStart() {
        target.setInvisible(true);
        target.setInvulnerable(true);
        target.setNoDrag(true);
        var dashVelocityMultiplier = ConfigManager.getServerConfig().dashVelocityMultiplier();
        dashVelocity = target.getRotationVector().multiply(dashVelocityMultiplier, 0.2, dashVelocityMultiplier);
        target.playSound(
                SoundEvents.BLOCK_DEEPSLATE_TILES_HIT,
                2f,
                -5f
        );
    }

    @Override
    public void tick() {
        if (target.getVelocity().lengthSquared() < 0.3 && age > 2) {
            this.aborted = true;
            return;
        }

        target.getWorld().getOtherEntities(target, target.getBoundingBox().expand(0.2)).forEach(entity -> {
            entity.setVelocity(entity.getRotationVector().negate().add(0, 0.75, 0));
            entity.velocityModified = true;
        });

        target.playSound(
                SoundEvents.UI_STONECUTTER_TAKE_RESULT,
                2f,
                -5f
        );


        if (!target.getWorld().isClient()) {
            ((ServerWorld) target.getWorld()).spawnParticles(
                    ParticleTypes.END_ROD,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    10,
                    0.5,
                    0.5,
                    0.5,
                    0.1
            );
        }

        target.setVelocity(dashVelocity);
        target.velocityModified = true;
        target.velocityDirty = true;
    }


    @Override
    public void onEnd() {
        target.setInvisible(false);
        target.setInvulnerable(false);
        target.setNoDrag(false);
    }

    @Override
    public boolean shouldContinue() {
        return age < ConfigManager.getServerConfig().dashSpellDuration() && !aborted;
    }
}

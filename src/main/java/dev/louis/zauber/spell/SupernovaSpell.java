package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class SupernovaSpell extends ZauberSpell<LivingEntity> {

    public SupernovaSpell() {
        super(SpellType.SUPERNOVA);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) {
        combust(source);
    }

    public void combust(SpellSource<LivingEntity> source) {
        //TODO: Forget Spell as it is a one time cast.
        //source.getSpellManager().forgetSpell(this.getType());
        spawnParticleRing(source, 60, 13.5f);
        spawnParticleRing(source, 20, 4);

        source.getCaster().playSound(
                SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
                4,
                1
        );

        source.getWorld().createExplosion(
                null,
                Explosion.createDamageSource(source.getWorld(), source.getCaster()),
                null,
                source.getPos().getX(),
                source.getCaster().getEyeY(),
                source.getPos().getZ(),
                ConfigManager.getServerConfig().supernovaExplosionPower(),
                true,
                World.ExplosionSourceType.MOB
        );

    }

    private static void spawnParticleRing(SpellSource<LivingEntity> source, int precision, float fullRadius) {
        for (double i = 0; i < Math.PI; i += Math.PI / precision) {
            double radius = Math.sin(i) * fullRadius;
            double y = Math.cos(i) * fullRadius;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / precision) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                ((ServerWorld) source.getWorld()).spawnParticles(
                        ParticleTypes.SMALL_FLAME,
                        source.getPos().getX() + x,
                        source.getCaster().getY() + y,
                        source.getPos().getZ() + z,
                        2,
                        0.05,
                        0.05,
                        0.05,
                        0
                );
            }
        }
    }
}

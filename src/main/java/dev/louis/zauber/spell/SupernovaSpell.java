package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ConfigManager;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class SupernovaSpell extends Spell {
    public SupernovaSpell(SpellType<? extends Spell> spellType) {
        super(spellType);
    }

    @Override
    public void cast() {
        combust();
    }

    public void combust() {
        if(getCaster() instanceof ServerPlayerEntity serverPlayer) {
            //Forget Spell as it is a one time cast.
            serverPlayer.getSpellManager().forgetSpell(this.getType());
            spawnParticleRing(serverPlayer, 60, 13.5f);
            spawnParticleRing(serverPlayer, 20, 4);

            serverPlayer.playSound(
                    SoundEvents.ENTITY_GENERIC_EXPLODE,
                    SoundCategory.PLAYERS,
                    4,
                    1
            );

            serverPlayer.getServerWorld().createExplosion(
                    null,
                    Explosion.createDamageSource(this.getCaster().getWorld(), this.getCaster()),
                    null,
                    this.getCaster().getX(),
                    this.getCaster().getEyeY(),
                    this.getCaster().getZ(),
                    ConfigManager.getServerConfig().supernovaExplosionPower(),
                    true,
                    World.ExplosionSourceType.MOB
            );
        }
    }

    private static void spawnParticleRing(ServerPlayerEntity serverPlayer, int precision, float fullRadius) {
        for (double i = 0; i < Math.PI; i += Math.PI / precision) {
            double radius = Math.sin(i) * fullRadius;
            double y = Math.cos(i) * fullRadius;
            for (double a = 0; a < Math.PI * 2; a+= Math.PI / precision) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                serverPlayer.getServerWorld().spawnParticles(
                        ParticleTypes.SMALL_FLAME,
                        serverPlayer.getX() + x,
                        serverPlayer.getEyeY() + y,
                        serverPlayer.getZ() + z,
                        2,
                        0.05,
                        0.05,
                        0.05,
                        0
                );
            }
        }
    }

    @Override
    public void onEnd() {
        super.onEnd();
        if(this.getCaster().isAlive()) this.getCaster().kill();
    }

    @Override
    public int getDuration() {
        return 1;
    }
}

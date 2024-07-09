package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ConfigManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class WindExpelSpell extends Spell {
    int ticksSneaking = 0;

    public WindExpelSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        var velocity = new Vec3d(0, 1.1, 0);
        //if(getCaster().isSneaking()) velocity = velocity.multiply(1, 0.6, 1);
        this.getCaster().setVelocity(velocity);

        this.getCaster().velocityModified = true;
        //this.getCaster().velocityDirty = true;
    }

    @Override
    public void tick() {
        if (this.getCaster() instanceof ServerPlayerEntity serverPlayer) {
            if (serverPlayer.isSneaking() && this.age > 5) {
                serverPlayer.setVelocity(serverPlayer.getVelocity().multiply(0,0.7,0));
                serverPlayer.velocityModified = true;
                ticksSneaking++;
                if(ticksSneaking > 3) {
                    stop();
                }
                return;
            } else {
                serverPlayer.addVelocity(new Vec3d(0, ConfigManager.getServerConfig().windExpelSpellAcceleration(), 0));
                serverPlayer.velocityModified = true;
            }
            double sin = Math.sin(age/1.75f) * 2.5;
            double cos = Math.cos(age/1.75f) * 2.5;
            serverPlayer.getServerWorld().spawnParticles(
                    ParticleTypes.CLOUD,
                    serverPlayer.getX() + sin,
                    serverPlayer.getY(),
                    serverPlayer.getZ() + cos,
                    1,
                    0,
                    0,
                    0,
                    0
            );


            this.getCaster().playSound(SoundEvents.BLOCK_GLASS_HIT, 2f, -1f);

        }
    }

    @Override
    public void finish() {
        if(getCaster() instanceof ServerPlayerEntity serverPlayer) {
            if (this.wasInterrupted()) {
                this.getCaster().playSound(SoundEvents.ITEM_SHIELD_BREAK, 1f, -1f);
                return;
            }
            this.getCaster().playSound(SoundEvents.ENTITY_CAMEL_DASH, 1f, -1f);

            if(!this.getCaster().isOnGround()) {
                serverPlayer.setVelocity(serverPlayer.getVelocity().multiply(1, 0.7, 1));
                serverPlayer.velocityModified = true;
            }

            //serverPlayer.playSound(SoundEvents.ENTITY_CAMEL_DASH, SoundCategory.PLAYERS, 2f, -1f);
            serverPlayer.getServerWorld().spawnParticles(
                    ParticleTypes.SMOKE,
                    serverPlayer.getX(),
                    serverPlayer.getY(),
                    serverPlayer.getZ(),
                    5,
                    0,
                    1,
                    0,
                    0.1
            );
        }
    }

    @Override
    public int getDuration() {
        return ConfigManager.getServerConfig().windExpelSpellDuration();
    }
}

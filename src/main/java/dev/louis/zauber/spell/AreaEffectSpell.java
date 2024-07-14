package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import static java.lang.Math.*;

public abstract class AreaEffectSpell extends Spell {
    private final ParticleEffect particle;
    protected Box spellCastingBox;

    public AreaEffectSpell(
            SpellType<? extends AreaEffectSpell> spellType,
            PlayerEntity caster,
            ParticleEffect particle
    ) {
        super(spellType, caster);
        this.particle = particle;
    }

    @Override
    public void cast() {
        this.spellCastingBox = getSpellCastingBox(this.getCaster());
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getCaster() instanceof ServerPlayerEntity serverPlayer) {
            spawnParticles(serverPlayer.getServerWorld());
            BlockPos.stream(spellCastingBox).forEach(blockPos -> affect(serverPlayer.getServerWorld(), blockPos));
            serverPlayer.getWorld().getOtherEntities(getCaster(), spellCastingBox).forEach(this::affect);
        }
    }

    /**
     * This method can be overridden to run something when an Entity is affected by the spell.
     */
    protected void affect(Entity entity) {
        if(entity instanceof LivingEntity livingEntity && livingEntity.isMobOrPlayer()) entity.damage(getDamageSource(), this.getDamage());
    }

    protected int getDamage() {
        return 1;
    }

    /**
     * This method can be overridden to run something when an Entity is affected by the spell.
     */
    protected void affect(ServerWorld serverWorld, BlockPos blockPos) {
    }

    private static Box getSpellCastingBox(PlayerEntity player) {
        Vec3d playerRotation = player.getRotationVec(1.0f).normalize();
        double x = playerRotation.x;
        double z = playerRotation.z;

        double absX = abs(x);
        double absZ = abs(z);

        final double threshold = 0.4;
        final double minLockRotation = 0.35;

        if(absX + absZ < threshold) {
            x = copySign(max(absX, minLockRotation), x);
            z = copySign(max(absZ, minLockRotation), z);
        }
        Vec3d adjustedRotation = new Vec3d(x, 0, z);
        final double multiplier = 3;
        final double yOffset = -0.35;

        return player.getBoundingBox().stretch(adjustedRotation).expand(1.0, -0.5, 1.0).offset(adjustedRotation.multiply(multiplier).add(0, yOffset, 0));
    }

    protected void spawnParticles(ServerWorld world) {
        for (double x = spellCastingBox.minX; x < spellCastingBox.maxX; x = x + 0.7) {
            for (double y = spellCastingBox.minY; y < spellCastingBox.maxY; y = y + 0.7) {
                for (double z = spellCastingBox.minZ; z < spellCastingBox.maxZ; z = z + 0.7) {
                    world.spawnParticles(particle, x, y, z, 1, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }
    }

    public DamageSource getDamageSource() {
        return this.getCaster().getWorld().getDamageSources().playerAttack(this.getCaster());
    }
}

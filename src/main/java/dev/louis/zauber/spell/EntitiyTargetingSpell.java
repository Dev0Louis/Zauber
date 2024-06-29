package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ConfigManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class EntitiyTargetingSpell extends Spell {
    private Entity entity;

    public EntitiyTargetingSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);

        var optionalEntity = getTargetedEntity(caster,);
        optionalEntity.ifPresent(entity -> this.entity = entity);
    }

    @Override
    public void cast() {

    }

    //Is not null in cast.
    public Entity castedOn() {
        return entity;
    }

    @Override
    public boolean isCastable() {
        return castedOn() != null && super.isCastable();
    }

    public static Optional<Entity> getTargetedEntity(Entity entity) {
        int maxDistance = ConfigManager.getServerConfig().entityTargetingDistance();
        Vec3d startPos = entity.getEyePos();
        Vec3d rotation = entity.getRotationVec(1.0F).multiply(maxDistance);
        Vec3d endPos = startPos.add(rotation);
        Box box = entity.getBoundingBox().stretch(rotation).expand(1.0);
        int squaredMaxDistance = maxDistance * maxDistance;
        Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.canHit();

        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, startPos, endPos, box, predicate, squaredMaxDistance);
        if (entityHitResult == null) {
            return Optional.empty();
        } else {
            return startPos.squaredDistanceTo(entityHitResult.getPos()) > (double) squaredMaxDistance ? Optional.empty() : Optional.of(entityHitResult.getEntity());
        }
    }
}

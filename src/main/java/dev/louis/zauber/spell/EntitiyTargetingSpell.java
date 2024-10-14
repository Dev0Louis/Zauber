package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.spell.type.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class EntitiyTargetingSpell extends ZauberSpell<LivingEntity> {

    public EntitiyTargetingSpell(SpellType<?> type) {
        super(type);
    }

    public Optional<Entity> getTargetedEntity(SpellSource<LivingEntity> source) {
        if (source.getCaster() instanceof LivingEntity entity) {
            int maxDistance =  1/*ConfigManager.getServerConfig().entityTargetingDistance()*/;
            Vec3d startPos = entity.getEyePos();
            Vec3d rotation = entity.getRotationVec(1.0F).multiply(maxDistance);
            Vec3d endPos = startPos.add(rotation);
            Box box = entity.getBoundingBox().stretch(rotation).expand(1.0);
            int squaredMaxDistance = maxDistance * maxDistance;
            Predicate<Entity> predicate = entityx -> !entityx.isSpectator() && entityx.canHit();

            EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, startPos, endPos, box, predicate, squaredMaxDistance);
            if (entityHitResult != null) {
                return startPos.squaredDistanceTo(entityHitResult.getPos()) > (double) squaredMaxDistance ? Optional.empty() : Optional.of(entityHitResult.getEntity());
            }
        }
        return Optional.empty();
    }
}

package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.entity.HailStoneEntity;
import dev.louis.zauber.entity.TotemOfIceEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HailStormSpell extends BlockTargetingSpell {
    public HailStormSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {

    }

    @Override
    public void tick() {
        World world = caster.getWorld();
        final int size = 8;
        for (int x = -size; x < size; x += 2) {
            for (int z = -size; z < size; z += 2) {
                if (world.getRandom().nextFloat() < 0.75f) continue;
                HailStoneEntity hailStoneEntity = HailStoneEntity.TYPE.create(world);
                hailStoneEntity.setOwner(this.getCaster());
                hailStoneEntity.setPosition(pos.toCenterPos().add(x, 30 + world.getRandom().nextDouble(), z));
                Vec3d randomVelocity = new Vec3d(world.getRandom().nextDouble() - .5, world.getRandom().nextDouble() - .5, world.getRandom().nextDouble() - .5).multiply(0.5);
                Vec3d velocity = pos.toCenterPos().subtract(hailStoneEntity.getPos()).normalize().add(randomVelocity);
                hailStoneEntity.setVelocity(velocity);
                hailStoneEntity.castedWithIceTotem = Zauber.hasTotem(this.caster, TotemOfIceEntity.TYPE);
                world.spawnEntity(hailStoneEntity);
            }
        }
    }

    @Override
    public int getDuration() {
        return 30;
    }
}

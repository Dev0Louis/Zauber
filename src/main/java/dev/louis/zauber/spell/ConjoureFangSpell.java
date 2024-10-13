package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.spell.type.SpellType;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.entity.TotemOfDarknessEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ConjoureFangSpell extends ZauberSpell<LivingEntity> {
    public ConjoureFangSpell() {
        super(SpellType.CONJOURE_FANG);
    }

    @Override
    public void cast(SpellSource<LivingEntity> source) {
        var caster = source.getCaster();
        Vec3d rotation = caster.getRotationVecClient();
        var hasTotemOfDarkness = Zauber.hasTotem(caster, TotemOfDarknessEntity.TYPE);
        for (int i = 0; i < (hasTotemOfDarkness ? 20 : 15); i++) {
            this.conjureFangs(source.getWorld(), source.getPos().getX() + rotation.x * (i + 1), source.getPos().getY(), source.getPos().getZ() + rotation.z * (i + 1), caster);
            if (hasTotemOfDarkness) {
                this.conjureFangs(source.getWorld(), source.getPos().getX() + rotation.x * (i + 1) - 1, source.getPos().getY(), source.getPos().getZ() + rotation.z * (i + 1) - 1, caster);
                this.conjureFangs(source.getWorld(), source.getPos().getX() + rotation.x * (i + 1) + 1, source.getPos().getY(), source.getPos().getZ() + rotation.z * (i + 1) + 1, caster);
            }
        }
    }

    private void conjureFangs(World world, double x, double y, double z, LivingEntity owner) {
        BlockPos.Mutable mutable = BlockPos.ofFloored(x, y, z).mutableCopy();
        final int MAX_RECURSION_DEPTH = 5;
        int recursion = 0;

        if (world.getBlockState(mutable).isSolidBlock(world, mutable.add(0, -1, 0))) {
            while (world.getBlockState(mutable).isSolidBlock(world, mutable)) {
                if (recursion > MAX_RECURSION_DEPTH) return;

                mutable.move(0, 1, 0);
                recursion++;
            }
        } else {
            while (world.getBlockState(mutable.down()).isAir()) {
                if (recursion > MAX_RECURSION_DEPTH) return;
                mutable.move(0, -1, 0);
                recursion++;
            }
        }
        world.spawnEntity(new EvokerFangsEntity(world, mutable.getX(), mutable.getY(), mutable.getZ(), 0, 0, owner));
        world.emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, mutable.getY(), z), GameEvent.Emitter.of(owner));
    }
}

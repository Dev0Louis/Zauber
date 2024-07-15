package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.entity.TotemOfDarknessEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ConjoureFangSpell extends Spell {
    public ConjoureFangSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        Vec3d rotation = this.getCaster().getRotationVecClient();
        var hasTotemOfDarkness = Zauber.hasTotem(this.getCaster(), TotemOfDarknessEntity.TYPE);
        for (int i = 0; i < (hasTotemOfDarkness ? 20 : 15); i++) {
            this.conjureFangs(this.getCaster().getWorld(), this.getCaster().getX() + rotation.x * (i + 1), this.getCaster().getY(), this.getCaster().getZ() + rotation.z * (i + 1));
            if (hasTotemOfDarkness) {
                this.conjureFangs(this.getCaster().getWorld(), this.getCaster().getX() + rotation.x * (i + 1) - 1, this.getCaster().getY(), this.getCaster().getZ() + rotation.z * (i + 1) - 1);
                this.conjureFangs(this.getCaster().getWorld(), this.getCaster().getX() + rotation.x * (i + 1) + 1, this.getCaster().getY(), this.getCaster().getZ() + rotation.z * (i + 1) + 1);
            }
        }
    }

    private void conjureFangs(World world, double x, double y, double z) {
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
        this.getCaster().getWorld().spawnEntity(new EvokerFangsEntity(this.getCaster().getWorld(), mutable.getX(), mutable.getY(), mutable.getZ(), 0, 0, this.getCaster()));
        this.getCaster().getWorld().emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, mutable.getY(), z), GameEvent.Emitter.of(this.getCaster()));
    }
}

package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.entity.HauntingDamageEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

/**
 * The logic for this spell spawns {@link HauntingDamageEntity} which do the actual "logic".
 */
public class RefusalOfDeathSpell extends Spell {
    public RefusalOfDeathSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {

    }

    private void addHuntingDamage(DamageSource damageSource, float damageAmount) {
        var random = this.caster.getRandom();
        var x = random.nextBetween(-10, 10);
        var y = random.nextBetween(-10, 10);
        var z = random.nextBetween(-10, 10);
        HauntingDamageEntity hauntingDamageEntity = new HauntingDamageEntity(HauntingDamageEntity.TYPE, caster.getWorld());
        hauntingDamageEntity.setPosition(this.caster.getPos().add(x, y, z));
        hauntingDamageEntity.setAttack(damageSource, damageAmount);
        hauntingDamageEntity.setOwner(this.caster);
        caster.getWorld().spawnEntity(hauntingDamageEntity);
    }

    @Override
    public int getDuration() {
        return 20 * 30;
    }

    public void onDamage(DamageSource source, float amount) {
        addHuntingDamage(source, amount);
    }
}

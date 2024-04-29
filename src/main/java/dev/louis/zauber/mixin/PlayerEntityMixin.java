package dev.louis.zauber.mixin;

import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.zauber.spell.RefusalOfDeathSpell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    public void applyDamage(DamageSource source, float amount) {
        var activeSpells = ((NebulaPlayer) this).getSpellManager().getActiveSpells();
        var refusalOfDeathSpells = activeSpells.stream().filter(spell -> spell instanceof RefusalOfDeathSpell).map(spell -> (RefusalOfDeathSpell) spell).toList();
        if (!refusalOfDeathSpells.isEmpty()) {
            refusalOfDeathSpells.forEach(refusalOfDeathSpell -> refusalOfDeathSpell.onDamage(source, amount));
            return;
        }
        super.applyDamage(source, amount);
    }
}

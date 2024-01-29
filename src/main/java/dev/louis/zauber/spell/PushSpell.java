package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import net.minecraft.util.math.Vec3d;

public class PushSpell extends TargetingSpell {
    public PushSpell(SpellType<? extends Spell> spellType) {
        super(spellType);
    }

    @Override
    public void cast() {
        var pulledPlayer = castedOn();
        if(pulledPlayer == null)return;
        Vec3d velocity = this.getCaster().getPos().subtract(pulledPlayer.getPos()).normalize().negate();
        pulledPlayer.setVelocity(velocity);
        pulledPlayer.velocityModified = true;
    }
}

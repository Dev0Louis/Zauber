package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.nebula.api.spell.quick.SpellException;
import dev.louis.zauber.entity.SpellArrowEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ArrowSpell implements Spell<LivingEntity> {

    private static final float MANA_CONSUMPTION_PER_ARROW = 0.05f;

    @Override
    public void cast(SpellSource<LivingEntity> spellSource) throws SpellException {
        if (/*spellSource.isClient()*/ spellSource.getCaster().getWorld().isClient()) return;

        var caster = spellSource.getCaster();
        var world = caster.getWorld();
        var loc = caster.getEyePos();
            caster.getServer().executeSync(() -> {
                for (int x = 0; x < 50; x++) {
                    try(var t1 = Transaction.openOuter()) {
                        float extracted = caster.getManaManager().extractMana(MANA_CONSUMPTION_PER_ARROW, t1);
                        if (extracted < MANA_CONSUMPTION_PER_ARROW) return;
                        SpellArrowEntity arrow = new SpellArrowEntity(world, caster, new ItemStack(Items.ARROW), this);

                        Vec3d vec3d = caster.getOppositeRotationVector(1.0F);
                        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0 * 0.017453292F, vec3d.x, vec3d.y, vec3d.z);
                        Vec3d vec3d2 = caster.getRotationVec(1.0F);
                        Vector3f vector3f = vec3d2.toVector3f().rotate(quaternionf);
                        ((ProjectileEntity) arrow).setVelocity(vector3f.x(), vector3f.y(), vector3f.z(), 1.7f, 10f);
                        arrow.setPosition(loc);
                        world.spawnEntity(arrow);
                        t1.commit();
                    }
                }
        });
    }
}

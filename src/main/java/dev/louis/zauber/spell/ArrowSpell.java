package dev.louis.zauber.spell;

import dev.louis.zauber.accessor.ArrowEntityAccessor;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ArrowSpell extends Spell {
    public ArrowSpell(SpellType<? extends Spell> spellType) {
        super(spellType);
    }

    @Override
    public void cast() {
        var caster = getCaster();
        var world = caster.getWorld();
        var loc = caster.getEyePos();
        if(caster instanceof ServerPlayerEntity serverPlayer) {
            for (int x = -5; x < 5; x++) {
                for (int z = -5; z < 5; z++) {
                    serverPlayer.getServer().executeSync(() -> {
                        ArrowEntity arrow = new ArrowEntity(world, caster, new ItemStack(Items.ARROW));
                        ((ArrowEntityAccessor) arrow).zauber$shouldDamageOwner(false);

                        Vec3d vec3d = caster.getOppositeRotationVector(1.0F);
                        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(0 * 0.017453292F, vec3d.x, vec3d.y, vec3d.z);
                        Vec3d vec3d2 = caster.getRotationVec(1.0F);
                        Vector3f vector3f = vec3d2.toVector3f().rotate(quaternionf);
                        ((ProjectileEntity)arrow).setVelocity(vector3f.x(), vector3f.y(), vector3f.z(), 1.7f, 10f);
                        arrow.setPosition(loc);
                        world.spawnEntity(arrow);
                    });


                }
            }
        }
    }

}

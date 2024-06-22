package dev.louis.zauber.item;

import dev.louis.zauber.entity.ManaArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ManaBowItem extends BowItem {
    public ManaBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getRange() {
        return 15;
    }

    public PersistentProjectileEntity createArrow(ArrowItem item, World world, ItemStack stack, LivingEntity shooter) {
        ManaArrowEntity arrowEntity = new ManaArrowEntity(world, shooter, stack.copyWithCount(1));
        //arrowEntity.initFromStack(stack);
        return arrowEntity;
    }
}

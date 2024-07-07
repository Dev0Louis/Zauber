package dev.louis.zauber.item;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.entity.ManaArrowEntity;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ManaBowItem extends BowItem implements PolymerItem, PolymerKeepModel, PolymerClientDecoded  {
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

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if (Zauber.isClientModded(player)) return this;
        return Items.ARROW;
    }
}

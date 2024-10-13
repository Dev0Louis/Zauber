package dev.louis.zauber.entity;

import dev.louis.zauber.Zauber;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpellArrowEntity extends PersistentProjectileEntity {
    private static final ItemStack DEFAULT_STACK = new ItemStack(Items.ARROW);
    public static final EntityType<SpellArrowEntity> TYPE = EntityType.Builder.<SpellArrowEntity>create(SpellArrowEntity::new, SpawnGroup.MISC).dimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20).build("spell_arrow");

    @Nullable
    private final Entity caster;

    public SpellArrowEntity(EntityType<? extends SpellArrowEntity> entityType, World world) {
        super(entityType, world);
        caster = null;
    }

    public SpellArrowEntity(World world, LivingEntity owner, ItemStack stack, @NotNull Entity caster) {
        super(TYPE, owner, world, stack, null);
        this.caster = caster;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (caster != null && caster.equals(entityHitResult.getEntity())) return;
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.discard();
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return DEFAULT_STACK;
    }

    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        if (Zauber.isClientModded(player)) return TYPE;
        return EntityType.ARROW;
    }
}

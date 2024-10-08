package dev.louis.zauber.entity;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.zauber.Zauber;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
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

public class SpellArrowEntity extends PersistentProjectileEntity implements PolymerClientDecoded, PolymerKeepModel, PolymerEntity {
    private static final ItemStack DEFAULT_STACK = new ItemStack(Items.ARROW);
    public static final EntityType<SpellArrowEntity> TYPE = EntityType.Builder.<SpellArrowEntity>create(SpellArrowEntity::new, SpawnGroup.MISC).dimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20).build("spell_arrow");

    @Nullable
    private final Spell spell;

    public SpellArrowEntity(EntityType<? extends SpellArrowEntity> entityType, World world) {
        super(entityType, world);
        this.spell = null;
    }

    public SpellArrowEntity(World world, LivingEntity owner, ItemStack stack, @NotNull Spell spell) {
        super(TYPE, owner, world, stack, null);
        this.spell = spell;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (spell != null && spell.getCaster().equals(entityHitResult.getEntity())) return;
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

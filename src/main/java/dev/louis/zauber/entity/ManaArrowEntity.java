package dev.louis.zauber.entity;

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
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ManaArrowEntity extends PersistentProjectileEntity implements PolymerClientDecoded, PolymerKeepModel, PolymerEntity {
    private static final ItemStack DEFAULT_STACK = new ItemStack(Items.ARROW);

    public static final EntityType<ManaArrowEntity> TYPE = EntityType.Builder.<ManaArrowEntity>create(ManaArrowEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20).build("mana_arrow");

    public ManaArrowEntity(EntityType<? extends ManaArrowEntity> entityType, World world) {
        super(entityType, world, DEFAULT_STACK);
    }

    public ManaArrowEntity(World world, LivingEntity owner, ItemStack stack) {
        super(TYPE, owner, world, stack);
    }


    public ManaArrowEntity(World world, double x, double y, double z, ItemStack stack) {
        super(TYPE, x, y, z, world, stack);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {

    }


    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult) hitResult);
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));
        }
    }

    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        if(Zauber.isClientModded(player)) return TYPE;
        return EntityType.ARROW;
    }
}

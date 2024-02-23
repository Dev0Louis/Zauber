package dev.louis.zauber.entity;

import dev.louis.nebula.api.spell.Spell;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpellArrowEntity extends ArrowEntity implements PolymerClientDecoded, PolymerKeepModel, PolymerEntity {
    public static final EntityType<SpellArrowEntity> TYPE = EntityType.Builder.<SpellArrowEntity>create(SpellArrowEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20).build("spell_arrow");

    @Nullable
    private final Spell spell;

    public SpellArrowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
        super(entityType, world);
        this.spell = null;
    }

    public SpellArrowEntity(World world, LivingEntity owner, ItemStack stack, @NotNull Spell spell) {
        super(world, owner, stack);
        this.spell = spell;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if(spell != null && spell.getCaster().equals(entityHitResult.getEntity())) return;
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.discard();
    }

    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return TYPE;
        //if(Zauber.isClientModded(player)) return ZauberEntityType.SPELL_ARROW;
        //return EntityType.ARROW;
    }
}

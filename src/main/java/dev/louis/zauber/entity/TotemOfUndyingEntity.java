package dev.louis.zauber.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class TotemOfUndyingEntity extends FollowingEntity {
    public static final EntityType<TotemOfUndyingEntity> TYPE = FabricEntityTypeBuilder
            .<TotemOfUndyingEntity>create(SpawnGroup.MISC, TotemOfUndyingEntity::new)
            .build();

    public TotemOfUndyingEntity(EntityType<?> type, World world) {
        super(type, world, Items.TOTEM_OF_UNDYING.getDefaultStack());
    }

    @Override
    public boolean isActive(LivingEntity livingEntity) {
        return true;
    }
}

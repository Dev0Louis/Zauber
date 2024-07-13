package dev.louis.zauber.entity;

import dev.louis.zauber.item.TotemOfIceItem;
import dev.louis.zauber.item.ZauberItems;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.World;

public class TotemOfIceEntity extends FollowingEntity {
    public static final EntityType<TotemOfIceEntity> TYPE = FabricEntityTypeBuilder
            .<TotemOfIceEntity>create(SpawnGroup.MISC, TotemOfIceEntity::new)
            .build();

    public TotemOfIceEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public TotemOfIceEntity(World world, LivingEntity owner) {
        super(TYPE, world, owner, ZauberItems.TOTEM_OF_ICE.getDefaultStack());
    }

    @Override
    public boolean isActive(LivingEntity livingEntity) {
        return TotemOfIceItem.isActive(livingEntity);
    }
}

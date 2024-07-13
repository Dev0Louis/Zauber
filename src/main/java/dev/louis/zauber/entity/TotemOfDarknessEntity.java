package dev.louis.zauber.entity;

import dev.louis.zauber.item.TotemOfDarknessItem;
import dev.louis.zauber.item.ZauberItems;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.World;

public class TotemOfDarknessEntity extends FollowingEntity {
    public static final EntityType<TotemOfDarknessEntity> TYPE = FabricEntityTypeBuilder
            .<TotemOfDarknessEntity>create(SpawnGroup.MISC, TotemOfDarknessEntity::new)
            .build();

    public TotemOfDarknessEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public TotemOfDarknessEntity(World world, LivingEntity owner) {
        super(TYPE, world, owner, ZauberItems.TOTEM_OF_DARKNESS.getDefaultStack());
    }

    @Override
    public boolean isActive(LivingEntity livingEntity) {
        return TotemOfDarknessItem.isActive(livingEntity);
    }
}

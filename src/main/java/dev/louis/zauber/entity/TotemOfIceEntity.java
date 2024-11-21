package dev.louis.zauber.entity;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.item.TotemOfIceItem;
import dev.louis.zauber.item.ZauberItems;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class TotemOfIceEntity extends FollowingEntity {
    public static final EntityType<TotemOfIceEntity> TYPE = FabricEntityTypeBuilder
            .<TotemOfIceEntity>create(SpawnGroup.MISC, TotemOfIceEntity::new)
            .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, "totem_of_ice")));

    public TotemOfIceEntity(EntityType<?> type, World world) {
        super(type, world, ZauberItems.TOTEM_OF_ICE.getDefaultStack());
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isActive(LivingEntity livingEntity) {
        return TotemOfIceItem.isActive(livingEntity);
    }
}

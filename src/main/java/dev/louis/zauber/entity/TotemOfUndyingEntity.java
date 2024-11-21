package dev.louis.zauber.entity;

import dev.louis.zauber.Zauber;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class TotemOfUndyingEntity extends FollowingEntity {
    public static final EntityType<TotemOfUndyingEntity> TYPE = FabricEntityTypeBuilder
            .<TotemOfUndyingEntity>create(SpawnGroup.MISC, TotemOfUndyingEntity::new)
            .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Zauber.MOD_ID, "totem_of_undying")));

    public TotemOfUndyingEntity(EntityType<?> type, World world) {
        super(type, world, Items.TOTEM_OF_UNDYING.getDefaultStack());
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isActive(LivingEntity livingEntity) {
        return true;
    }
}

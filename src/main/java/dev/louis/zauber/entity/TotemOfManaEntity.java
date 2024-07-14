package dev.louis.zauber.entity;

import dev.emi.trinkets.api.TrinketsApi;
import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.zauber.item.TotemOfIceItem;
import dev.louis.zauber.item.ZauberItems;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

public class TotemOfManaEntity extends FollowingEntity {
    public static final EntityType<TotemOfManaEntity> TYPE = FabricEntityTypeBuilder
            .<TotemOfManaEntity>create(SpawnGroup.MISC, TotemOfManaEntity::new)
            .build();

    public TotemOfManaEntity(EntityType<?> type, World world) {
        super(type, world, ZauberItems.TOTEM_OF_MANA.getDefaultStack());
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient()) {
            super.tick();
            if (this.age % 20 == 0 && (this.getOwner() instanceof NebulaPlayer player) && player.getManaManager().getMana() < player.getManaManager().getMaxMana()) {
                TrinketsApi.getTrinketComponent(this.getOwner()).ifPresent(component -> {
                    component.getEquipped(ZauberItems.TOTEM_OF_MANA)
                            .stream().map(Pair::getRight).filter(stack -> stack.getDamage() + 1 < stack.getMaxDamage()).findAny().ifPresent(stack -> {
                                stack.damage(1, (ServerWorld) this.getWorld(), this.getOwner() instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null, item -> {});
                                player.getManaManager().addMana(1);
                            });
                });
            }
        }
    }

    @Override
    public boolean isActive(LivingEntity livingEntity) {
        return TotemOfIceItem.isActive(livingEntity);
    }
}

package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.helper.EffectHelper;
import dev.louis.zauber.helper.ParticleHelper;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.stream.Stream;

public class TeleportToLodestoneRitual extends Ritual implements ManaPullingRitual {
    private int collectedMana;
    private boolean failedToFindMana;

    public TeleportToLodestoneRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        super(world, ritualStoneBlockEntity);
    }

    @Override
    public void tick() {
        if (this.age % 20 == 0) {
            ritualStoneBlockEntity.acquireManaPool(2).ifPresentOrElse(manaPool -> {
                manaPool.apply();
                this.collectedMana += 2;
            }, () -> this.failedToFindMana = true);
            this.getAffectedEntities().forEach(livingEntity -> {
                ParticleHelper.spawnParticles(
                        (ServerWorld) livingEntity.getWorld(),
                        livingEntity.getPos(),
                        ParticleTypes.PORTAL,
                        3,
                        0.2f,
                        0.2f
                );
            });
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void finish() {
        var itemSacrificersWithEnderPearl = ritualStoneBlockEntity.getNonEmptyItemSacrificers()
                .filter(itemSacrificerBlockEntity -> itemSacrificerBlockEntity.getStoredStack().isOf(Items.ENDER_PEARL)).toList();
        if (collectedMana >= 40 && !itemSacrificersWithEnderPearl.isEmpty()) {
            itemSacrificersWithEnderPearl.forEach(itemSacrificer -> itemSacrificer.setStoredStack(ItemStack.EMPTY));
            var globalPos = ritualStoneBlockEntity.getStoredStack().get(DataComponentTypes.LODESTONE_TRACKER).target();
            if (globalPos.isPresent()) {
                BlockPos blockPos = globalPos.get().pos();
                this.getAffectedEntities()
                        // Limit the amount of entities that will be transported by the amount of ender pearls given
                        .limit(itemSacrificersWithEnderPearl.size()).forEach(livingEntity -> {
                    livingEntity.teleport(
                            blockPos.getX(),
                            blockPos.getY() + 1,
                            blockPos.getZ(),
                            true
                    );
                });
            }
        }
        EffectHelper.playBreakItemEffect((ServerWorld) world, pos, ritualStoneBlockEntity.getStoredStack());
        ritualStoneBlockEntity.setStoredStack(ItemStack.EMPTY);
    }

    public Stream<LivingEntity> getAffectedEntities() {
        return world.getEntitiesByClass(
                LivingEntity.class,
                Box.of(pos.toCenterPos(), 3, 3, 3),
                EntityPredicates.EXCEPT_SPECTATOR

        ).stream();
    }

    @Override
    public boolean shouldStop() {
        return collectedMana >= 40 || failedToFindMana;
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        var ritualItemStack = ritualStoneBlockEntity.getStoredStack();

        boolean hasOneEnderPearl = ritualStoneBlockEntity.getAvailableItemStacks().anyMatch(itemStack -> itemStack.isOf(Items.ENDER_PEARL));
        if(!isCompassWithLodestoneInSameWorld(world, ritualItemStack) || !hasOneEnderPearl) return null;
        return new TeleportToLodestoneRitual(world, ritualStoneBlockEntity);
    }

    public static boolean isCompassWithLodestoneInSameWorld(World world, ItemStack itemStack) {
        if (itemStack.contains(DataComponentTypes.LODESTONE_TRACKER)) {
            var component = itemStack.get(DataComponentTypes.LODESTONE_TRACKER);
            var target = component.target();
            if (target.map(registryKey -> registryKey.dimension().equals(world.getRegistryKey())).orElse(false)) {
                //We are in the same dimension.
                if (!component.tracked()) {
                    return false;
                }
                BlockPos blockPos = target.get().pos();
                return world.isInBuildLimit(blockPos) && ((ServerWorld) world).getPointOfInterestStorage().hasTypeAt(PointOfInterestTypes.LODESTONE, blockPos);
            }
        }
        return false;
    }


}

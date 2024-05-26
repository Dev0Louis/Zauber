package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.helper.EffectHelper;
import dev.louis.zauber.helper.ParticleHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.stream.Stream;

public class TeleportToLodestoneRitual extends Ritual {
    private int collectedMana;
    private boolean failedToFindMana;

    public TeleportToLodestoneRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        super(world, ritualStoneBlockEntity);
    }

    @Override
    public void tick() {
        if (this.age % 20 == 0) {
            ritualStoneBlockEntity.getFullManaStorages().findAny().ifPresentOrElse(blockPos -> {
                world.setBlockState(blockPos, Blocks.CAULDRON.getDefaultState());
                // we add 2 as we clear the entire cauldron
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
            NbtCompound nbt = ritualStoneBlockEntity.getStoredStack().getNbt();
            if (nbt != null) {
                BlockPos blockPos = NbtHelper.toBlockPos(nbt.getCompound("LodestonePos"));
                this.getAffectedEntities()
                        // Limit the amount of entities that will be transported by the amount of enderpearls given
                        .limit(itemSacrificersWithEnderPearl.size()).forEach(livingEntity -> {
                    livingEntity.teleport(
                            blockPos.getX(),
                            blockPos.getY() + 1,
                            blockPos.getZ()
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

        var fullManaCauldrons = ritualStoneBlockEntity.getFullManaStorages();
        boolean hasOneEnderPearl = ritualStoneBlockEntity.getAvailableItemStacks().anyMatch(itemStack -> itemStack.isOf(Items.ENDER_PEARL));
        if(fullManaCauldrons.count() < 12 || !isCompassWithLodestoneInSameWorld(world, ritualItemStack) || !hasOneEnderPearl) return null;
        return new TeleportToLodestoneRitual(world, ritualStoneBlockEntity);
    }

    public static boolean isCompassWithLodestoneInSameWorld(World world, ItemStack itemStack) {
        if (CompassItem.hasLodestone(itemStack)) {
            NbtCompound nbt = itemStack.getNbt();
            if (nbt == null) return false;
            if (CompassItem.getLodestoneDimension(nbt).map(registryKey -> registryKey.equals(world.getRegistryKey())).orElse(false)) {
                //We are in the same dimension.
                if (nbt.contains("LodestoneTracked") && !nbt.getBoolean("LodestoneTracked")) {
                    return false;
                }
                BlockPos blockPos = NbtHelper.toBlockPos(nbt.getCompound("LodestonePos"));
                if (world.isInBuildLimit(blockPos) && ((ServerWorld)world).getPointOfInterestStorage().hasTypeAt(PointOfInterestTypes.LODESTONE, blockPos)) {
                    return true;
                }
            }
        }
        return false;
    }


}

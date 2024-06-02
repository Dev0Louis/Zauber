package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.ItemSacrificerBlockEntity;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.helper.EffectHelper;
import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.helper.SoundHelper;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SmeltingRitual extends Ritual {

    private final RecipeManager.MatchGetter<Inventory, SmeltingRecipe> matchGetter = RecipeManager.createCachedMatchGetter(RecipeType.SMELTING);;

    @Nullable
    private BlockPos itemSacrificerPos;
    private int fuelTicks;

    private int cookTime;
    private int cookProgress;

    private int inactivityTicks;

    public SmeltingRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity, int fuelTicks) {
        super(world, ritualStoneBlockEntity);
        this.fuelTicks = fuelTicks;
    }

    private static Optional<Integer> getCookTime(World world, ItemStack itemStack) {
        return world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(itemStack), world).map(recipe -> recipe.value().getCookingTime());
    }

    public static Optional<Integer> getFuelTime(ItemConvertible item) {
        Integer integer = FuelRegistry.INSTANCE.get(item);
        if (integer == null) return Optional.empty();
        return Optional.of(integer);
    }

    public static Optional<ItemStack> cook(World world, ItemStack itemStack) {
        return world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(itemStack), world).map(recipe -> recipe.value().getResult(world.getRegistryManager()));
    }

    @Override
    public void tick() {
        this.tryBurnEntities();
        if (itemSacrificerPos == null) {
            ritualStoneBlockEntity.getNonEmptyItemSacrificers().filter(itemSacrificer -> getCookTime(this.world, itemSacrificer.getStoredStack()).map(integer -> fuelTicks - integer > 0).orElse(false)).findAny().ifPresent(itemSacrificer -> {

                itemSacrificerPos = itemSacrificer.getPos();
                //this is safe as we checḱ that as a requirement for an itemSacrificer to be selected
                //noinspection OptionalGetWithoutIsPresent
                cookTime = getCookTime(world, itemSacrificer.getStoredStack()).get();
            });
        }

        if (itemSacrificerPos != null) {
            ParticleHelper.spawnConnection(
                    (ServerWorld) world,
                    ritualStoneBlockEntity.getPos().toCenterPos(), itemSacrificerPos.up().toCenterPos(),
                    ParticleTypes.FLAME, 10
            );
            SoundHelper.playSound(
                    (ServerWorld) world,
                    itemSacrificerPos.up().toCenterPos(),
                    SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                    SoundCategory.AMBIENT,
                    1f, 1f
            );
            cookProgress++;

            if (cookProgress >= cookTime) {
                world.getBlockEntity(itemSacrificerPos, ItemSacrificerBlockEntity.TYPE).ifPresent(itemSacrificer -> {
                    cook(world, itemSacrificer.getStoredStack()).map(ItemStack::copy).ifPresent(itemSacrificer::setStoredStack);
                    fuelTicks = fuelTicks - cookTime;
                });
                itemSacrificerPos = null;
                cookProgress = 0;
            }

        } else {
            this.inactivityTicks++;
        }
    }

    private void tryBurnEntities() {
        world.getEntitiesByClass(
                LivingEntity.class,
                Box.of(this.ritualStoneBlockEntity.getPos().toCenterPos(), 3, 3, 3),
                EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR
        ).forEach(entity -> {
            if (this.age % 10 == 0) {
                ParticleHelper.spawnParticleLine(
                        (ServerWorld) world,
                        ritualStoneBlockEntity.getPos().toCenterPos(),
                        entity.getPos(),
                        ParticleTypes.FLAME,
                        5
                );
            }
            entity.setOnFireFor(15);
            entity.setFireTicks(entity.getFireTicks() + 1);

            entity.damage(world.getDamageSources().inFire(), 1);

        });
    }

    @Override
    public void onStart() {

    }

    @Override
    public void finish() {

    }

    @Override
    public boolean shouldStop() {
        return fuelTicks <= 0 || inactivityTicks > 20 * 5;
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        ItemStack ritualItemStack = ritualStoneBlockEntity.getStoredStack();
        var fuelTime = getFuelTime(ritualItemStack.getItem());


        //TODO: Check if any items are available with this fuelTime
        if(fuelTime.isEmpty()) return null;

        ritualStoneBlockEntity.setStoredStack(ItemStack.EMPTY);
        EffectHelper.playBreakItemEffect((ServerWorld) world, ritualStoneBlockEntity.getPos(), ritualItemStack);
        return new SmeltingRitual(world, ritualStoneBlockEntity, fuelTime.get());
    }
}

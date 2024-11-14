package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.ItemSacrificerBlockEntity;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.helper.EffectHelper;
import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.helper.SoundHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SmeltingRitual extends Ritual {

    private final ServerRecipeManager.MatchGetter<SingleStackRecipeInput, ? extends AbstractCookingRecipe> matchGetter;

    @Nullable
    private BlockPos itemSacrificerPos;
    private int fuelTicks;

    private int cookTime;
    private int cookProgress;

    private int inactivityTicks;

    public SmeltingRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity, int fuelTicks) {
        super(world, ritualStoneBlockEntity);
        this.fuelTicks = fuelTicks;
        this.matchGetter = ServerRecipeManager.createCachedMatchGetter(RecipeType.SMELTING);

    }

    private Optional<Integer> getCookTime(ServerWorld world, ItemStack itemStack) {
        return matchGetter.getFirstMatch(new SingleStackRecipeInput(itemStack), world).map(recipe -> recipe.value().getCookingTime());
    }

    public static Optional<Integer> getFuelTime(
            ItemStack item,
            FuelRegistry fuelRegistry
    ) {
        int fuelTicks = fuelRegistry.getFuelTicks(item);
        if (fuelTicks == 0) return Optional.empty();
        return Optional.of(fuelTicks);
    }

    public Optional<ItemStack> cook(World world, ItemStack itemStack) {
        return matchGetter.getFirstMatch(new SingleStackRecipeInput(itemStack), (ServerWorld) world).map(recipe -> recipe.value().craft(null, world.getRegistryManager()));
    }

    @Override
    public void tick() {
        this.tryBurnEntities();
        if (itemSacrificerPos == null) {
            ritualStoneBlockEntity.getNonEmptyItemSacrificers().filter(itemSacrificer -> getCookTime((ServerWorld) this.world, itemSacrificer.getStoredStack()).map(integer -> fuelTicks - integer > 0).orElse(false)).findAny().ifPresent(itemSacrificer -> {

                itemSacrificerPos = itemSacrificer.getPos();
                //this is safe as we checḱ that as a requirement for an itemSacrificer to be selected
                //noinspection OptionalGetWithoutIsPresent
                cookTime = getCookTime((ServerWorld) world, itemSacrificer.getStoredStack()).get();
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

            entity.damage((ServerWorld) world, world.getDamageSources().inFire(), 1);

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

    public static Ritual tryStart(ServerWorld world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        ItemStack ritualItemStack = ritualStoneBlockEntity.getStoredStack();
        var fuelTime = getFuelTime(ritualItemStack, world.getFuelRegistry());

        if (fuelTime.isEmpty()) return null;

        ritualStoneBlockEntity.setStoredStack(ItemStack.EMPTY);
        EffectHelper.playBreakItemEffect(world, ritualStoneBlockEntity.getPos(), ritualItemStack);
        return new SmeltingRitual(world, ritualStoneBlockEntity, fuelTime.get());
    }
}

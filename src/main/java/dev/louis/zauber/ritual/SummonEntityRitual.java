package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.ItemSacrificerBlockEntity;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.helper.EffectHelper;
import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.helper.SoundHelper;
import dev.louis.zauber.tag.ZauberItemTags;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.BiFunction;
import java.util.function.Function;

public class SummonEntityRitual extends Ritual {
    public static final Vector3f RED_COLOR = new Vector3f(0.9f, 0, 0);
    private final BiFunction<World, ItemStack, Entity> entityFunction;
    private final Ingredient mainIngredient;

    @Nullable
    private BlockPos itemSacrificerPos;
    private int blood;

    public SummonEntityRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity, BiFunction<World, ItemStack, Entity> entityFunction, Ingredient mainIngredient) {
        super(world, ritualStoneBlockEntity);
        this.entityFunction = entityFunction;
        this.mainIngredient = mainIngredient;
    }

    @Override
    public void tick() {
        if (blood > 7) {
            if (this.age % 10 == 0) {
                ParticleHelper.spawnParticles(
                        (ServerWorld) world,
                        this.pos.up().toCenterPos(),
                        ParticleTypes.ENCHANT,
                        5,
                        0.4f,
                        1
                );
                SoundHelper.playSound(
                        (ServerWorld) world,
                        this.pos.up().toCenterPos(),
                        SoundEvents.BLOCK_SNIFFER_EGG_CRACK,
                        SoundCategory.BLOCKS,
                        2f,
                        -world.getRandom().nextFloat() * 3
                );
            }
        }

        if (itemSacrificerPos == null) {
            itemSacrificerPos = ritualStoneBlockEntity.getNonEmptyItemSacrificers()
                    .filter(itemSacrificer -> itemSacrificer.getStoredStack().isIn(ZauberItemTags.BLOOD_CONTAINING)).map(BlockEntity::getPos).findAny().orElse(null);
        } else {
            ParticleHelper.spawnParticleLine(
                    (ServerWorld) world,
                    pos.toCenterPos().add(0, 0.8f, 0),
                    itemSacrificerPos.toCenterPos().add(0, 0.8f, 0),
                    new DustParticleEffect(RED_COLOR, 1),
                    10
            );
            if (this.age % 10 == 0) {
                SoundHelper.playSound(
                        (ServerWorld) world,
                        this.pos.up().toCenterPos(),
                        SoundEvents.ENTITY_ZOGLIN_STEP,
                        SoundCategory.BLOCKS,
                        2f,
                        -world.getRandom().nextFloat() * 3
                );

            }

            if (this.age % 30 == 0) {
                world.getBlockEntity(itemSacrificerPos, ItemSacrificerBlockEntity.TYPE).ifPresent(itemSacrificerBlock -> {
                    EffectHelper.playBloodItemEffect((ServerWorld) world, itemSacrificerPos.up(), itemSacrificerBlock.getStoredStack());
                    itemSacrificerBlock.setStoredStack(ItemStack.EMPTY);
                    this.blood++;
                    this.itemSacrificerPos = null;
                });
            }
        }

    }

    @Override
    public void onStart() {

    }

    @Override
    public void finish() {
        if (this.mainIngredient.test(this.ritualStoneBlockEntity.getStoredStack()) && blood >= 10) {
            Entity entity = entityFunction.apply(world, this.ritualStoneBlockEntity.getStoredStack());
            entity.setPosition(pos.up().toCenterPos());
            world.spawnEntity(entity);

            ParticleHelper.spawn20Particles(
                    (ServerWorld) world,
                    entity.getPos().add(0, 0.75, 0),
                    new DustParticleEffect(RED_COLOR, 3)
            );
            this.ritualStoneBlockEntity.setStoredStack(ItemStack.EMPTY);
            SoundHelper.playSound(
                    (ServerWorld) world,
                    this.pos.up().toCenterPos(),
                    SoundEvents.ENTITY_ZOGLIN_STEP,
                    SoundCategory.BLOCKS,
                    2f,
                    world.getRandom().nextFloat() * 3
            );
        }
    }

    @Override
    public boolean shouldStop() {
        return this.age > 800 || blood >= 10;
    }

    public static class Starter implements Ritual.Starter {
        private final BiFunction<World, ItemStack, Entity> entityFunction;
        private final Ingredient mainIngredient;

        public Starter(EntityType<?> entityType, Ingredient mainIngredient) {
            this(entityType::create, mainIngredient);
        }

        public Starter(Function<World, Entity> entityFunction, Ingredient mainIngredient) {
            this((world1, ingredient) -> entityFunction.apply(world1), mainIngredient);
        }

        public Starter(BiFunction<World, ItemStack, Entity> entityFunction, Ingredient mainIngredient) {
            this.entityFunction = entityFunction;
            this.mainIngredient = mainIngredient;
        }


        @Override
        public Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
            if (!mainIngredient.test(ritualStoneBlockEntity.getStoredStack())) return null;
            return new SummonEntityRitual(world, ritualStoneBlockEntity, entityFunction, mainIngredient);
        }
    }
}

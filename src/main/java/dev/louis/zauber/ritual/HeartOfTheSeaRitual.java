package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.ItemSacrificerBlockEntity;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.helper.EffectHelper;
import dev.louis.zauber.helper.ParticleHelper;
import dev.louis.zauber.helper.SoundHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class HeartOfTheSeaRitual extends Ritual {
    private final Vector3f BLUE_COLOR = new Vector3f(0, 0, 0.5f);
    private boolean finished;
    @Nullable
    private BlockPos nextItemSacrificerPos;
    private int connectionTime;
    private int crystalsCollected;

    public HeartOfTheSeaRitual(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        super(world, ritualStoneBlockEntity);
    }

    @Override
    public void tick() {
        if (nextItemSacrificerPos != null) {
            if (age % 5 == 0) {
                ParticleHelper.spawnParticleLine(
                        (ServerWorld) world,
                        pos.toCenterPos().add(0, 0.8, 0),
                        nextItemSacrificerPos.toCenterPos().add(0, 0.8, 0),
                        new DustParticleEffect(BLUE_COLOR, 0.9f),
                        16
                );
                SoundHelper.playSound(
                        (ServerWorld) world,
                        pos,
                        SoundEvents.ENTITY_AXOLOTL_IDLE_WATER,
                        SoundCategory.BLOCKS,
                        0.4f,
                        2
                );
            }

            if (age % 30 == 0) {
                SoundHelper.playSound(
                        (ServerWorld) world,
                        pos,
                        SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT,
                        SoundCategory.BLOCKS,
                        1,
                        1
                );
            }


            connectionTime++;
            if (connectionTime > 80) {
                world.getBlockEntity(nextItemSacrificerPos, ItemSacrificerBlockEntity.TYPE)
                        .filter(itemSacrificer -> itemSacrificer.getStoredStack().isOf(Items.PRISMARINE_CRYSTALS))
                        .ifPresent(itemSacrificer -> {
                            EffectHelper.playBreakItemEffect(
                                    (ServerWorld) world,
                                    nextItemSacrificerPos,
                                    itemSacrificer.getStoredStack()
                            );
                            SoundHelper.playSound(
                                    (ServerWorld) world,
                                    pos,
                                    SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET,
                                    SoundCategory.BLOCKS,
                                    1,
                                    1
                            );
                            itemSacrificer.setStoredStack(ItemStack.EMPTY);
                        });
                this.findNextItemSacrificer();
                this.connectionTime = 0;
                this.crystalsCollected++;
            }

        }
    }

    @Override
    public void onStart() {
        findNextItemSacrificer();
    }

    private void findNextItemSacrificer() {
        nextItemSacrificerPos = ritualStoneBlockEntity.getNonEmptyItemSacrificers()
                .filter(itemSacrificer -> itemSacrificer.getStoredStack().isOf(Items.PRISMARINE_CRYSTALS))
                .map(BlockEntity::getPos).findAny().orElse(null);
    }

    @Override
    public void finish() {
        if (this.crystalsCollected >= 4) {
            SoundHelper.playSound(
                    (ServerWorld) world,
                    this.pos,
                    SoundEvents.BLOCK_CONDUIT_ACTIVATE,
                    SoundCategory.BLOCKS,
                    1,
                    -2
            );
            this.ritualStoneBlockEntity.setStoredStack(Items.HEART_OF_THE_SEA.getDefaultStack());
        }
    }

    @Override
    public boolean shouldStop() {
        return this.crystalsCollected >= 4 || this.age > 800;
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        var ritualItemStack = ritualStoneBlockEntity.getStoredStack();

        List<ItemStack> prismarineCrystals = ritualStoneBlockEntity.getAvailableItemStacks().filter(itemStack -> itemStack.isOf(Items.PRISMARINE_CRYSTALS)).toList();
        if(!(ritualItemStack.isOf(Items.NAUTILUS_SHELL)) || prismarineCrystals.size() < 4) return null;
        return new HeartOfTheSeaRitual(world, ritualStoneBlockEntity);
    }
}

package dev.louis.zauber.block;

import com.mojang.serialization.MapCodec;
import dev.louis.zauber.helper.ShutUpAboutBlockStateModels;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.tag.ZauberPotionTags;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.event.GameEvent;

@ShutUpAboutBlockStateModels
public class ManaCauldron extends AbstractCauldronBlock {
    public static final MapCodec<ManaCauldron> CODEC = createCodec(ManaCauldron::new);
    public static final IntProperty MANA_LEVEL = IntProperty.of("mana_level", 1, 2);

    public static final CauldronBehavior.CauldronBehaviorMap MANA_CAULDRON_BEHAVIOR = CauldronBehavior.createMap("mana");

    static {
        MANA_CAULDRON_BEHAVIOR.map().put(Items.POTION, ((state, world, pos, player, hand, stack) -> {
            if (!world.isClient && state.get(MANA_LEVEL) < 2 && stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion().map(potionRegistryEntry -> potionRegistryEntry.isIn(ZauberPotionTags.MANA)).orElse(false)) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, state.with(MANA_LEVEL, state.get(MANA_LEVEL) + 1));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ItemActionResult.success(world.isClient);
        }));

        MANA_CAULDRON_BEHAVIOR.map().put(ZauberItems.TOTEM_OF_MANA, ((state, world, pos, player, hand, stack) -> {
            if (stack.isDamaged()) {
                stack.setDamage(Math.max(0, stack.getDamage() - 10));
                var manaLevel = state.get(MANA_LEVEL) - 1;
                if (manaLevel == 0) {
                    world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                } else {
                    world.setBlockState(pos, state.with(MANA_LEVEL, manaLevel));
                }
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return ItemActionResult.CONSUME;
            }
            return ItemActionResult.CONSUME;
        }));
    }

    protected ManaCauldron(Settings settings) {
        super(settings, MANA_CAULDRON_BEHAVIOR);
        setDefaultState(getDefaultState().with(MANA_LEVEL, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MANA_LEVEL);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return CauldronBlock.OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return CauldronBlock.RAYCAST_SHAPE;
    }

    @Override
    public boolean isFull(BlockState state) {
        return state.get(MANA_LEVEL) >= 2;
    }

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> getCodec() {
        return CODEC;
    }
}

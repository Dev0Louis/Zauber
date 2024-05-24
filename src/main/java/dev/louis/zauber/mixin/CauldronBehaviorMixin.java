package dev.louis.zauber.mixin;

import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.tag.ZauberPotionTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {
    @Inject(
            method = "method_32222",
            at = @At(value = "HEAD")
    )
    private static void addManaPotionBehavior(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient && PotionUtil.getPotion(stack).getRegistryEntry().isIn(ZauberPotionTags.MANA)) {
            Item item = stack.getItem();
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            player.incrementStat(Stats.USE_CAULDRON);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            world.setBlockState(pos, ZauberBlocks.MANA_CAULDRON.getDefaultState());
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
    }

}

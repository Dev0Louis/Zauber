package dev.louis.zauber.ritual;

import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.ritual.mana.ManaPool;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ManaBowRitual extends Ritual implements ManaPullingRitual {
    private final ManaPool manaPool;

    protected ManaBowRitual(World world, RitualStoneBlockEntity blockEntity, ManaPool manaPool) {
        super(world, blockEntity);
        this.manaPool = manaPool;
    }

    @Override
    public void tick() {
        if (age % 5 == 0) {
            world.playSound(null, this.pos, SoundEvents.ENTITY_ARROW_HIT, SoundCategory.PLAYERS, 1, -4);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public boolean shouldStop() {
        return age > 40;
    }

    @Override
    public void finish() {
        //ItemSacrificerBlockEntity with a call goat horn.


        if (manaPool.isValid() && ManaBowRitual.isBow(ritualStoneBlockEntity.getStoredStack())) {
            ritualStoneBlockEntity.setStoredStack(ItemStack.EMPTY);
            manaPool.apply();
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, ZauberItems.MANA_BOW.getDefaultStack(), 0, 0.3f, 0));
        }
    }

    @Override
    public Stream<Position> getConnections() {
        return this.manaPool.manaReferences().stream().map(manaReference -> manaReference.source().toCenterPos());
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        var ritualItemStack = ritualStoneBlockEntity.getStoredStack();

        var manaPool = ritualStoneBlockEntity.acquireManaPool(2);
        if (!ManaBowRitual.isBow(ritualItemStack) || manaPool.isEmpty()) return null;
        return new ManaBowRitual(world, ritualStoneBlockEntity, manaPool.get());
    }


    public static boolean isBow(@Nullable ItemStack itemStack) {
        return itemStack.isOf(Items.BOW);
    }
}

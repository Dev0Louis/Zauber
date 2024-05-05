package dev.louis.zauber.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class BlockEntityWithItemStack extends BlockEntity {
    protected ItemStack storedStack = ItemStack.EMPTY;

    public BlockEntityWithItemStack(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("storedStack", this.storedStack.writeNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        setStoredStack(ItemStack.fromNbt(nbt.getCompound("storedStack")));
    }

    public void setStoredStack(ItemStack storedStack) {
        this.markDirty();
        this.storedStack = storedStack;
    }

    public ItemStack getStoredStack() {
        return storedStack;
    }
}

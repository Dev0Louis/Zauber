package dev.louis.zauber.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class BlockEntityWithItemStack extends BlockEntity implements SingleStackInventory {
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

    @Override
    public ItemStack getStack() {
        return storedStack;
    }

    @Override
    public ItemStack decreaseStack(int count) {
        ItemStack itemStack = this.storedStack.split(count);
        if (this.storedStack.isEmpty()) {
            this.storedStack = ItemStack.EMPTY;
        }
        return itemStack;

    }

    @Override
    public void setStack(ItemStack stack) {
        this.storedStack = stack;
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }
}

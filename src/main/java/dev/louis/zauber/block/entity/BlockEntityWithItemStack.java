package dev.louis.zauber.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class BlockEntityWithItemStack extends BlockEntity implements SingleStackInventory {
    protected ItemStack storedStack = ItemStack.EMPTY;

    public BlockEntityWithItemStack(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.storedStack.isEmpty()) {
            nbt.put("storedStack", this.storedStack.encodeAllowEmpty(registryLookup));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("Item", NbtElement.COMPOUND_TYPE)) {
            ItemStack.fromNbt(registryLookup, nbt.get("storedStack")).ifPresent(this::setStoredStack);
        }
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
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
}

package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.ZauberBlocks;
import dev.louis.zauber.helper.SoundHelper;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class ItemSacrificerBlockEntity extends BlockEntityWithItemStack {
    public static final BlockEntityType<ItemSacrificerBlockEntity> TYPE = BlockEntityType.Builder.create(ItemSacrificerBlockEntity::new, ZauberBlocks.ITEM_SACRIFICER).build(null);
    private ItemDisplayElement itemDisplayElement;
    private BlockDisplayElement basePlate;
    private BlockDisplayElement secondPlate;
    public boolean firstTick = true;
    private int ticksSinceItemsAdded;
    private ElementHolder holder;

    public ItemSacrificerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    private void init() {
        this.holder = new ElementHolder();
        ChunkAttachment.ofTicking(holder, (ServerWorld) world, pos.up());

        basePlate = new BlockDisplayElement(Blocks.QUARTZ_SLAB.getDefaultState());
        basePlate.setScale(new Vector3f(1, 0.2f, 1));
        basePlate.setOffset(new Vec3d(-0.5, -1.5, -0.5));
        holder.addElement(basePlate);

        secondPlate = new BlockDisplayElement(Blocks.QUARTZ_SLAB.getDefaultState());
        secondPlate.setScale(new Vector3f(0.5f, 0.2f, 0.5f));
        holder.addElement(secondPlate);

        itemDisplayElement = new ItemDisplayElement();
        itemDisplayElement.setOffset(new Vec3d(0, -0.25, 0));
        this.itemDisplayElement.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        this.itemDisplayElement.setItem(this.getStoredStack());
        holder.addElement(itemDisplayElement);
        this.firstTick = false;
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, ItemSacrificerBlockEntity itemSacrificer) {
        if (itemSacrificer.firstTick) {
            itemSacrificer.init();
        }
        itemSacrificer.secondPlate.setScale(new Vector3f(0.75f, 0.2f, 0.75f));
        itemSacrificer.secondPlate.setOffset(new Vec3d(-0.375, -1.4, -0.375));


        if (itemSacrificer.storedStack == ItemStack.EMPTY) return;

        if (itemSacrificer.storedStack.isEmpty()) {
            itemSacrificer.setStoredStack(ItemStack.EMPTY);
            return;
        }


        itemSacrificer.ticksSinceItemsAdded++;
        itemSacrificer.itemDisplayElement
                .setRightRotation(
                        RotationAxis.POSITIVE_Y.rotationDegrees(world.getTime() * ((itemSacrificer.storedStack.getItem().hashCode() % 40) / 40f))
                );


        itemSacrificer.itemDisplayElement.setOffset(new Vec3d(0, getItemOffset(itemSacrificer), 0));
        float multiplier = Math.min(1, itemSacrificer.ticksSinceItemsAdded / 10f);


        float size = (float) ((float) (Math.sin(itemSacrificer.ticksSinceItemsAdded / 50f) / 30) + 0.4) * multiplier;
        itemSacrificer.itemDisplayElement.setScale(new Vector3f(size, size, size));
    }

    public static double getItemOffset(ItemSacrificerBlockEntity itemSacrificer) {
        return -0.25 + (0.2 * Math.abs(Math.sin(itemSacrificer.ticksSinceItemsAdded / 100f)));
    }

    public ActionResult offerItemStack(PlayerEntity player, ItemStack offeredStack) {
        itemDisplayElement.setScale(new Vector3f(0, 0, 0));

        var sound = SoundEvents.BLOCK_SNIFFER_EGG_PLOP;
        var volume = 2;
        var pitch = 1;
        if (this.storedStack != ItemStack.EMPTY) {
            player.getInventory().offerOrDrop(this.storedStack);
            setStoredStack(ItemStack.EMPTY);
            //player.playSound(sound, SoundCategory.PLAYERS, volume, pitch);
            SoundHelper.playPlayerSound((ServerPlayerEntity) player, sound, volume, pitch);
            player.getItemCooldownManager().set(offeredStack.getItem(), 5);
        }

        if (offeredStack.isEmpty()) return ActionResult.FAIL;
        player.getItemCooldownManager().set(offeredStack.getItem(), 5);

        setStoredStack(offeredStack.copyWithCount(1));
        //player.playSound(sound, SoundCategory.PLAYERS, volume, pitch);
        SoundHelper.playPlayerSound((ServerPlayerEntity) player, sound, volume, pitch);

        offeredStack.decrement(1);
        player.getInventory().markDirty();
        return ActionResult.SUCCESS;
    }

    @Override
    public void setStoredStack(ItemStack itemStack) {
        super.setStoredStack(itemStack);
        this.ticksSinceItemsAdded = 0;
        if (this.itemDisplayElement != null) this.itemDisplayElement.setItem(this.storedStack);
    }

    @Override
    public void markRemoved() {
        if (holder != null) this.holder.destroy();
        super.markRemoved();
    }
}

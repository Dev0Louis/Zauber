package dev.louis.zauber.block.entity;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class ItemSacrificerBlockEntity extends BlockEntity {
    public static final BlockEntityType<ItemSacrificerBlockEntity> TYPE = null;//BlockEntityType.Builder.create(ItemSacrificerBlockEntity::new, ZauberBlocks.ITEM_SACRIFICER).build(null);
    public ItemStack storedStack = ItemStack.EMPTY;
    private ItemDisplayElement itemDisplayElement;
    public boolean firstTick = true;
    private int ticksSinceItemsAdded;

    public ItemSacrificerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    private void init() {
        var holder = new ElementHolder();
        ChunkAttachment.ofTicking(holder, (ServerWorld) world, pos.up());

        itemDisplayElement = new ItemDisplayElement();
        itemDisplayElement.setOffset(new Vec3d(0, -0.25, 0));
        this.itemDisplayElement.setBillboardMode(DisplayEntity.BillboardMode.FIXED);
        holder.addElement(itemDisplayElement);
        this.firstTick = false;
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, ItemSacrificerBlockEntity ritualStoneBlockEntity) {
        if (ritualStoneBlockEntity.firstTick) {
            ritualStoneBlockEntity.init();
        }

        if (ritualStoneBlockEntity.storedStack == ItemStack.EMPTY) return;

        if (ritualStoneBlockEntity.storedStack.isEmpty()) {
            ritualStoneBlockEntity.setStoredStack(ItemStack.EMPTY);
            return;
        }


        ritualStoneBlockEntity.ticksSinceItemsAdded++;
        ritualStoneBlockEntity.itemDisplayElement
                .setRightRotation(
                        RotationAxis.POSITIVE_Y.rotationDegrees(world.getTime() * ((ritualStoneBlockEntity.storedStack.getItem().hashCode() % 40) / 40f))
                );


        ritualStoneBlockEntity.itemDisplayElement.setOffset(new Vec3d(0, -0.25 + (0.2 * Math.abs(Math.sin(ritualStoneBlockEntity.ticksSinceItemsAdded / 100f))), 0));
        float multiplier = Math.min(1, ritualStoneBlockEntity.ticksSinceItemsAdded / 10f);


        float size = (float) ((float) (Math.sin(ritualStoneBlockEntity.ticksSinceItemsAdded / 50f) / 30) + 0.4) * multiplier;
        ritualStoneBlockEntity.itemDisplayElement.setScale(new Vector3f(size, size, size));
    }

    public ActionResult offerItemStack(PlayerEntity player, ItemStack offeredStack) {
        itemDisplayElement.setScale(new Vector3f(0, 0, 0));

        var sound = SoundEvents.BLOCK_SNIFFER_EGG_PLOP;
        var volume = 2;
        var pitch = 1;
        if (this.storedStack != ItemStack.EMPTY) {
            player.getInventory().offerOrDrop(this.storedStack);
            setStoredStack(ItemStack.EMPTY);
            player.playSound(sound, SoundCategory.PLAYERS, volume, pitch);
            player.playSound(sound, volume, pitch);
            player.getItemCooldownManager().set(offeredStack.getItem(), 5);
            return ActionResult.FAIL;
        }

        if (offeredStack.isEmpty()) return ActionResult.FAIL;
        player.getItemCooldownManager().set(offeredStack.getItem(), 5);

        setStoredStack(offeredStack.copyWithCount(1));
        player.playSound(sound, SoundCategory.PLAYERS, volume, pitch);
        player.playSound(sound, volume, pitch);

        offeredStack.decrement(1);
        player.getInventory().markDirty();
        return ActionResult.SUCCESS;
    }

    public void setStoredStack(ItemStack itemStack) {
        this.storedStack = itemStack;
        this.ticksSinceItemsAdded = 0;
        this.itemDisplayElement.setItem(this.storedStack);
    }
}

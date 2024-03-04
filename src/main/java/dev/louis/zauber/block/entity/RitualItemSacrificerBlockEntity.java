package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.ZauberBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Quaternionf;

public class RitualItemSacrificerBlockEntity extends BlockEntity {
    public static final BlockEntityType<RitualItemSacrificerBlockEntity> TYPE = BlockEntityType.Builder.create(RitualItemSacrificerBlockEntity::new, ZauberBlocks.RITUAL_ITEM_SACRIFICER).build(null);
    public ItemStack storedStack = ItemStack.EMPTY;
    public boolean isDirty = false;
    public boolean firstTick = true;
    private DisplayEntity.ItemDisplayEntity itemDisplayEntity;


    public RitualItemSacrificerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, RitualItemSacrificerBlockEntity ritualStoneBlockEntity) {
        if (ritualStoneBlockEntity.firstTick) {
            ritualStoneBlockEntity.firstTick = false;
            ritualStoneBlockEntity.itemDisplayEntity = new DisplayEntity.ItemDisplayEntity(EntityType.ITEM_DISPLAY, world) {
                @Override
                public boolean shouldSave() {
                    return false;
                }
            };
            var vec3d = ritualStoneBlockEntity.getPos().up().toCenterPos();
            ritualStoneBlockEntity.itemDisplayEntity.setPos(vec3d.x, vec3d.y, vec3d.z);
            world.spawnEntity(ritualStoneBlockEntity.itemDisplayEntity);
        }
        var age = ritualStoneBlockEntity.itemDisplayEntity.age;
        var vec3d = ritualStoneBlockEntity.getPos().up().toCenterPos();
        //ritualStoneBlockEntity.itemDisplayEntity.setPos(vec3d.x, vec3d.y + (Math.sin(age / 40f) / 6), vec3d.z);
        //ritualStoneBlockEntity.itemDisplayEntity.setYaw(age);
        var c1 = (Math.sin(age / 10f) / 80) + 0.9;
        var c2 = Math.sin(age / 25f) * 2 * Math.PI;
        var quaternionf = new Quaternionf(0, 0, 0, c1);
        float size = (float) Math.abs(Math.sin(age / 100f) / 8) + 0.5f;
        //ritualStoneBlockEntity.itemDisplayEntity.getDataTracker().set(DisplayEntity.SCALE, new Vector3f(size, size, size));
        ritualStoneBlockEntity.itemDisplayEntity.getDataTracker().set(DisplayEntity.RIGHT_ROTATION, quaternionf);
        //ritualStoneBlockEntity.itemDisplayEntity.getDataTracker().set(DisplayEntity.LEFT_ROTATION, quaternionf);



        if(ritualStoneBlockEntity.isDirty) {
            ritualStoneBlockEntity.itemDisplayEntity.getStackReference(0).set(ritualStoneBlockEntity.storedStack);
            ritualStoneBlockEntity.isDirty = false;
        }
    }

    public ActionResult offerItemStack(PlayerEntity player, ItemStack offeredStack) {
        if (this.storedStack != ItemStack.EMPTY) {
            player.getInventory().offerOrDrop(this.storedStack);
            this.storedStack = ItemStack.EMPTY;
            this.isDirty = true;
        }

        if (offeredStack.isEmpty()) return ActionResult.FAIL;
        player.getItemCooldownManager().set(offeredStack.getItem(), 5);

        this.storedStack = offeredStack.copyWithCount(1);
        this.isDirty = true;

        offeredStack.decrement(1);
        return ActionResult.SUCCESS;
    }
}

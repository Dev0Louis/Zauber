package dev.louis.zauber.block.entity;

import dev.louis.zauber.block.ZauberBlocks;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ManaStorageBlockEntity extends BlockEntity {
    public static final BlockEntityType<ManaStorageBlockEntity> TYPE = BlockEntityType.Builder.create(ManaStorageBlockEntity::new, ZauberBlocks.MANA_STORAGE).build(null);
    private TextDisplayElement textDisplayElement;
    public boolean firstTick = true;
    private int ticksSinceItemsAdded;

    public ManaStorageBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    private void init() {
        var holder = new ElementHolder();
        ChunkAttachment.ofTicking(holder, (ServerWorld) world, pos.up());

        textDisplayElement = new TextDisplayElement();
        textDisplayElement.setOffset(new Vec3d(0, -0.25, 0));
        this.textDisplayElement.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
        holder.addElement(textDisplayElement);
        this.firstTick = false;
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, ManaStorageBlockEntity ritualStoneBlockEntity) {
        if (ritualStoneBlockEntity.firstTick) {
            ritualStoneBlockEntity.init();
        }

        ritualStoneBlockEntity.textDisplayElement.setOffset(new Vec3d(0, -0.25 + (0.2 * Math.abs(Math.sin(ritualStoneBlockEntity.ticksSinceItemsAdded / 100f))), 0));
    }
}

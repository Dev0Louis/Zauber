package dev.louis.zauber.entity;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.extension.PlayerEntityExtension;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

//Entity to reference Telekinesis
public class TelekinesisEntity extends Entity implements Ownable {
    PlayerEntity owner;
    public static final EntityType<TelekinesisEntity> TYPE = EntityType.Builder
            .<TelekinesisEntity>create(TelekinesisEntity::new, SpawnGroup.MISC)
            .dimensions(1, 1)
            .build();
    BlockState blockState;
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(TelekinesisEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    @Nullable
    NbtCompound blockEntityData;

    public TelekinesisEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public TelekinesisEntity(World world, Vec3d pos, BlockState state, @Nullable BlockEntity blockEntity, PlayerEntity owner) {
        super(TYPE, world);
        this.setPosition(pos);
        this.blockState = state;
        if (blockEntity != null) this.blockEntityData = blockEntity.createNbt(world.getRegistryManager());
        this.owner = owner;
        this.setFallingBlockPos(this.getBlockPos());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(BLOCK_POS, BlockPos.ORIGIN);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) return;

        //System.out.println(state);
        if (owner == null || ((PlayerEntityExtension) owner).zauber$getTelekinesisAffected().map(entity -> entity != TelekinesisEntity.this).orElse(true)) {

            if (tryPlace()) return;
            FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(
                    this.getWorld(),
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    this.blockState
            );
            fallingBlockEntity.setFallingBlockPos(this.getBlockPos());
            this.getWorld().spawnEntity(fallingBlockEntity);
            this.discard();
        }


        /*var target = this.owner.getEyePos().add(this.owner.getRotationVector().normalize().multiply(6).add(0, -.5, 0));
        this.setVelocity(this.getVelocity().multiply(0.75));
        var vel = target.subtract(this.getPos()).multiply(0.1);
        this.addVelocity(vel);
        this.move(MovementType.SELF, this.getVelocity());*/
    }

    private boolean tryPlace() {
        var block = blockState.getBlock();
        var blockPos = this.getBlockPos();
        BlockState blockState = this.getWorld().getBlockState(blockPos);
        if (!blockState.isOf(Blocks.MOVING_PISTON)) {
            boolean canReplace = blockState.canReplace(new AutomaticItemPlacementContext(this.getWorld(), blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
            boolean canPlaceAt = this.blockState.canPlaceAt(this.getWorld(), blockPos);
            if (canReplace && canPlaceAt) {
                if (this.blockState.contains(Properties.WATERLOGGED) && this.getWorld().getFluidState(blockPos).getFluid() == Fluids.WATER) {
                    this.blockState = this.blockState.with(Properties.WATERLOGGED, Boolean.valueOf(true));
                }

                if (this.getWorld().setBlockState(blockPos, this.blockState, Block.NOTIFY_ALL)) {
                    ((ServerWorld)this.getWorld())
                            .getChunkManager()
                            .chunkLoadingManager
                            .sendToOtherNearbyPlayers(this, new BlockUpdateS2CPacket(blockPos, this.getWorld().getBlockState(blockPos)));
                    this.discard();

                    if (this.blockEntityData != null && this.blockState.hasBlockEntity()) {
                        BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
                        if (blockEntity != null) {
                            NbtCompound nbtCompound = blockEntity.createNbt(this.getWorld().getRegistryManager());

                            for (String string : this.blockEntityData.getKeys()) {
                                nbtCompound.put(string, this.blockEntityData.get(string).copy());
                            }

                            try {
                                blockEntity.read(nbtCompound, this.getWorld().getRegistryManager());
                            } catch (Exception var15) {
                                Zauber.LOGGER.error("Failed to load block entity from falling block", (Throwable)var15);
                            }

                            blockEntity.markDirty();
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        blockState = BlockState.CODEC.decode(NbtOps.INSTANCE, nbt.get("telekinesisState")).getOrThrow().getFirst();
        if (nbt.contains("telekinesisBlockEntityData")) {
            blockEntityData = nbt.getCompound("telekinesisBlockEntityData");
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        var nbtElement = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, blockState).getOrThrow();
        nbt.put("telekinesisState", nbtElement);
        if (blockEntityData != null) {
            nbt.put("telekinesisBlockEntityData", blockEntityData);
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        return new EntitySpawnS2CPacket(this, entityTrackerEntry, Block.getRawIdFromState(this.getBlockState()));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.blockState = Block.getStateFromRawId(packet.getEntityData());
    }

    public void setFallingBlockPos(BlockPos pos) {
        this.dataTracker.set(BLOCK_POS, pos);
    }

    public BlockPos getFallingBlockPos() {
        return this.dataTracker.get(BLOCK_POS);
    }

    @Nullable
    @Override
    public Entity getOwner() {
        return owner;
    }

    public void loseOwner() {
        owner = null;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public void throwBlock() {
        FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(
                this.getWorld(),
                this.getX(),
                this.getY(),
                this.getZ(),
                this.blockState
        );
        fallingBlockEntity.setFallingBlockPos(this.getBlockPos());
        fallingBlockEntity.setVelocity(fallingBlockEntity.getPos().subtract(owner.getPos()).multiply(0.2));
        this.getWorld().spawnEntity(fallingBlockEntity);
        this.discard();
    }
}

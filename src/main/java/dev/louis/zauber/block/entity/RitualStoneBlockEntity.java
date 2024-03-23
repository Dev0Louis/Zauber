package dev.louis.zauber.block.entity;

import com.google.common.collect.ImmutableList;
import dev.louis.zauber.particle.ZauberParticleTypes;
import dev.louis.zauber.ritual.Ritual;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.poi.PointOfInterest;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Stream;

public class RitualStoneBlockEntity extends BlockEntity {
    public static final BlockEntityType<RitualStoneBlockEntity> TYPE = null;//BlockEntityType.Builder.create(RitualStoneBlockEntity::new, ZauberBlocks.RITUAL_STONE).build(null);
    private final ElementHolder holder;
    @Nullable
    private Ritual ritual;
    private Collection<BlockPos> itemSacrificers = new ArrayList<>();
    private final HashMap<ItemStack, ItemDisplayElement> collectedItems = new HashMap<>();
    protected final Random random = Random.create();
    private int age = 0;
    boolean hasInitialised = false;
    private State state;
    private int ticksTillRitual = 0;
    private int ticksTillFail = 0;
    private int itemCooldown = 0;

    public RitualStoneBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
        this.holder = new ElementHolder();
    }


    public void init() {
        ChunkAttachment.ofTicking(holder, (ServerWorld) world, pos.up());
    }

    public void tick(World world, BlockPos blockPos, BlockState state) {
        final double PI2 = 2 * Math.PI;

        if(!this.hasInitialised) this.init();
        age++;
        this.spawnConnectionParticle();
        var elements = holder.getElements();
        for (int i = 0; i < elements.size(); i++) {
            float size = elements.size();
            var x = Math.sin((i / size) * PI2 + (age / 30f)) * 1;
            var z = Math.cos((i / size) * PI2 + (age / 30f)) * 1;
            var element = (ItemDisplayElement) elements.get(i);
            element.setRightRotation(RotationAxis.POSITIVE_Y.rotation(age / 30f));
            double yMult = this.state == State.ACTIVE ? Math.sin(age * i) : 1;
            element.setOffset(new Vec3d(x, -0.25 * yMult, z));
        }
        if(this.state == State.COLLECTING) {
            if (this.age % 20 == 0) {
                var optional = this.itemSacrificers.stream().unordered()
                        .map(pos1 -> world.getBlockEntity(pos1, ItemSacrificerBlockEntity.TYPE)).filter(Optional::isPresent).map(Optional::get).filter(itemSacrificerBlockEntity -> !itemSacrificerBlockEntity.storedStack.isEmpty()).findAny();
                optional.ifPresentOrElse(itemSacrificerBlockEntity -> {
                    this.collectItem(itemSacrificerBlockEntity.storedStack);
                    itemSacrificerBlockEntity.setStoredStack(ItemStack.EMPTY);
                    world.playSound(
                            null,
                            itemSacrificerBlockEntity.getPos().up(),
                            SoundEvents.ENTITY_ENDER_EYE_DEATH,
                            SoundCategory.BLOCKS
                    );
                    world.syncWorldEvent(WorldEvents.EYE_OF_ENDER_BREAKS, itemSacrificerBlockEntity.getPos().up(), 0);
                }, () -> this.state = State.INACTIVE);
            }
        } else if (this.state == State.READY) {
            if (ritual == null) {
                Ritual.RITUALS.forEach(ritualStarter -> {
                    if (this.ritual != null) return;
                    this.ritual = ritualStarter.tryStart(world, this);
                });
                if (ritual == null) {
                    fail(false);
                    return;
                }
            }

            /*if (ticksTillFail <= 0) {
                Ritual.RITUALS.forEach(ritualStarter -> {
                    if (this.ritual != null) return;
                    this.ritual = ritualStarter.tryStart(world, this);
                });
                if (this.ritual == null) {
                    ticksTillFail = 20 * 5;
                } else {
                    this.state = State.ACTIVE;
                    ticksTillRitual = 40;
                }
            } else {
                for (int i = 0; i < elements.size(); i++) {
                    var element = (ItemDisplayElement) elements.get(i);
                    element.setGlowing((ticksTillFail + i) % 8 < 4);
                }
                ticksTillFail--;
                if (ticksTillFail <= 0) fail();
            }*/
        } else if (this.state == State.ACTIVE) {

            if (ritual == null) {
                fail(true);
                return;
            };
            ritual.baseTick();
            var list = new ArrayList<>(this.collectedItems.keySet());
            Collections.shuffle(list);
            var optionalItemStack = list.stream().findAny();
            optionalItemStack.ifPresent(itemStack -> {
                if (ritual.offer(itemStack)) {
                    elements.remove(this.collectedItems.remove(itemStack));
                } else {
                    fail(true);
                }
            });

            if(ritual.shouldStop()) {
                this.state = State.INACTIVE;
                ritual.finish();
                ritual = null;
                return;
            }

        }
    }

    private void fail(boolean hardFail) {
        if (hardFail) {
            world.createExplosion(
                    null,
                    world.getDamageSources().explosion(null),
                    new ExplosionBehavior(),
                    this.pos.toCenterPos(),
                    3,
                    false,
                    World.ExplosionSourceType.BLOCK
            );
        }
        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_PISTON_EXTEND,
                SoundCategory.BLOCKS,
                3,
                -40
        );
        dropCollectedItems();
        this.state = State.INACTIVE;
    }

    public void onBlockClicked(PlayerEntity player) {
        if (this.state == State.ACTIVE) return;

        if (player.isSneaking()) {
            dropCollectedItems();
            this.state = State.INACTIVE;
            return;
        }

        this.itemSacrificers = getItemSacrificersPos().toList();
        this.state = this.collectedItems.isEmpty() ? State.COLLECTING : State.READY;
    }

    private void collectItem(ItemStack itemStack) {
        var itemDisplayElement = new ItemDisplayElement();
        itemDisplayElement.setScale(new Vector3f(0.4f, 0.4f, 0.4f));
        itemDisplayElement.setItem(itemStack);
        holder.addElement(itemDisplayElement);
        this.collectedItems.put(itemStack, itemDisplayElement);
    }


    private void dropCollectedItems() {
        Vec3d vec3d = this.getPos().up().toCenterPos();
        this.collectedItems.forEach((itemStack, element) -> {
            double velocityX = MathHelper.nextBetween(this.random, -0.2F, 0.2F);
            double velocityY = MathHelper.nextBetween(this.random, 0.3F, 0.7F);
            double velocityZ = MathHelper.nextBetween(this.random, -0.2F, 0.2F);
            ItemEntity itemEntity = new ItemEntity(this.getWorld(), vec3d.getX(), vec3d.getY(), vec3d.getZ(), itemStack, velocityX, velocityY, velocityZ);
            this.getWorld().spawnEntity(itemEntity);
            this.holder.removeElement(element);
        });
        this.collectedItems.clear();
        //List.copyOf(this.holder.getElements()).forEach(this.holder::removeElement);
    }

    protected void spawnConnectionParticle() {
        final Vec3d ritualPos = pos.toCenterPos();
        if(world.getTime() % 15 == 0) {
            getRitualBlockPos().forEach(pointOfInterest -> {
                final int steps = 10;
                final Vec3d sacrificerPos = pointOfInterest.getPos().toCenterPos();
                for (int i = 0; i < steps; i++) {
                    var delta = (double) i / steps;
                    delta = (delta * 0.9) + 0.1;
                    var x = MathHelper.lerp(delta, sacrificerPos.x, ritualPos.x);
                    var y = MathHelper.lerp(delta, sacrificerPos.y, ritualPos.y);
                    var z = MathHelper.lerp(delta, sacrificerPos.z, ritualPos.z);
                    Vec3d pos = new Vec3d(x, y, z);
                    ((ServerWorld)world).spawnParticles(
                            ZauberParticleTypes.MANA_RUNE,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            1,
                            0,
                            0,
                            0,
                            0.0
                    );
                }
            });
        }
    }

    public Collection<ItemStack> getCollectedItems() {
        return ImmutableList.copyOf(collectedItems.keySet());
    }

    public boolean removeCollectedItem(ItemStack itemStack) {
        var removed = this.collectedItems.remove(itemStack);
        if (removed != null) {
            this.holder.removeElement(removed);
            return true;
        }
        return false;
    }

    public Stream<PointOfInterest> getRitualBlockPos() {
        /*if (world instanceof ServerWorld serverWorld) {
            return serverWorld.getPointOfInterestStorage()
                    .getInSquare(
                            poiType -> poiType.matchesKey(ZauberPointOfInterestTypes.RITUAL_BLOCKS_KEY),
                            this.pos,
                            20,
                            PointOfInterestStorage.OccupationStatus.ANY
                    );
        }*/
        return Stream.empty();
    }

    public Stream<BlockPos> getItemSacrificersPos() {
        if (world instanceof ServerWorld serverWorld) {
            return getRitualBlockPos().map(poi -> serverWorld.getBlockEntity(poi.getPos(), ItemSacrificerBlockEntity.TYPE)).filter(Optional::isPresent).map(Optional::get).filter(blockEntity -> blockEntity.storedStack != ItemStack.EMPTY).map(BlockEntity::getPos);
        }
        return Stream.empty();
    }

    public Stream<ItemSacrificerBlockEntity> getItemSacrificers() {
        if (world instanceof ServerWorld serverWorld) {
            return getItemSacrificersPos().map(pos1 -> serverWorld.getBlockEntity(pos1, ItemSacrificerBlockEntity.TYPE)).filter(Optional::isPresent).map(Optional::get);
        }
        return Stream.empty();
    }

    public enum State {
        INACTIVE,
        COLLECTING,
        READY,
        ACTIVE
    }
}

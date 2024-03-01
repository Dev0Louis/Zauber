package dev.louis.zauber.ritual;

import dev.louis.zauber.items.ZauberItems;
import dev.louis.zauber.particle.ZauberParticleTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

public class HorseRitual extends Ritual {
    private final HorseEntity horse;
    private final ItemEntity itemEntity;

    protected HorseRitual(World world, BlockPos ritualStonePos, LivingEntity initiator, HorseEntity horse, ItemEntity itemEntity) {
        super(world, ritualStonePos, initiator);
        this.horse = horse;
        this.itemEntity = itemEntity;
        if (!itemEntity.getStack().isOf(Items.GOAT_HORN)) {
            throw new IllegalStateException("Item Entity is not a goat horn. It is: " + itemEntity.getStack().getItem().toString()) ;
        }
    }

    @Override
    public void tick() {
        if(age % 5 == 0) {
            final int steps = 10;
            final Vec3d horsePos = horse.getPos();
            final Vec3d ritualPos = pos.toCenterPos();

            for (int i = 0; i < (steps + 1); i++) {
                //System.out.println(i);
                var x = MathHelper.lerp((double) i / steps, horsePos.x, ritualPos.x);
                var y = MathHelper.lerp((double) i / steps, horsePos.y, ritualPos.y);
                var z = MathHelper.lerp((double) i / steps, horsePos.z, ritualPos.z);
                Vec3d pos = new Vec3d(x, y + 0.6, z);
                ((ServerWorld)world).spawnParticles(
                        ZauberParticleTypes.MANA_RUNE,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        1,
                        0.3,
                        0.3,
                        0.3,
                        0.01
                );
            }
        }

        //ParticleUtil.spawnParticle(world, ritualStonePos, Direction.UP, ParticleTypes.ANGRY_VILLAGER, horse.getPos().subtract(ritualStonePos.toCenterPos()), 1);
    }

    @Override
    public void start() {

    }

    @Override
    public boolean shouldStop() {
        return age > 40;
    }

    @Override
    public void finish() {
        if (horse.isAlive() && itemEntity.isAlive()) {
            horse.discard();
            itemEntity.discard();
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, ZauberItems.SOUL_HORN.getDefaultStack(), 0, 0.3f, 0));
        }
    }

    public static Ritual tryStart(World world, BlockPos ritualStonePos, LivingEntity initiator) {
        var box = Box.of(ritualStonePos.toCenterPos(), 32, 20, 32);

        HorseEntity horse = getNearestEntity(HorseEntity.class, ritualStonePos, box, world);
        ItemEntity goatHorn = getNearestEntity(
                ItemEntity.class,
                (itemEntity) -> itemEntity.getStack().isOf(Items.GOAT_HORN) && isCallGoatHorn(itemEntity.getStack()),
                ritualStonePos,
                box,
                world
        );
        if (horse == null || goatHorn == null) return null;

        return new HorseRitual(world, ritualStonePos, initiator, horse, goatHorn);
    }

    private static boolean isCallGoatHorn(ItemStack itemStack) {
        var optionalInstrument = getInstrument(itemStack);
        if (optionalInstrument.isPresent()) {
            RegistryKey<Instrument> instrument = optionalInstrument.get().getKey().get();
            return instrument.equals(Instruments.CALL_GOAT_HORN);
        } else {
            return false;
        }
    }

    private static Optional<? extends RegistryEntry<Instrument>> getInstrument(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null && nbtCompound.contains("instrument", NbtElement.STRING_TYPE)) {
            Identifier identifier = Identifier.tryParse(nbtCompound.getString("instrument"));
            if (identifier != null) {
                return Registries.INSTRUMENT.getEntry(RegistryKey.of(RegistryKeys.INSTRUMENT, identifier));
            }
        }

        Iterator<RegistryEntry<Instrument>> iterator = Registries.INSTRUMENT.iterateEntries(InstrumentTags.GOAT_HORNS).iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }


    @Nullable
    static <T extends Entity> T getNearestEntity(Class<? extends T> entityClass, BlockPos pos, Box box, World world) {
        return getNearestEntity(entityClass, (entity) -> true, pos, box, world);
    }

    @Nullable
    static <T extends Entity> T getNearestEntity(Class<? extends T> entityClass, Predicate<T> predicate, BlockPos pos, Box box, World world) {
        var entities = world.getEntitiesByClass(entityClass, box, entityOfClass -> true);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double distance = -1.0;
        T entity = null;

        for(T entity2 : entities) {
            if(!predicate.test(entity2))continue;
            double e = entity2.squaredDistanceTo(x, y, z);
            if (distance == -1.0 || e < distance) {
                distance = e;
                entity = entity2;
            }
        }

        return entity;
    }

}

package dev.louis.zauber.ritual;

import dev.louis.zauber.block.ManaCauldron;
import dev.louis.zauber.block.entity.RitualStoneBlockEntity;
import dev.louis.zauber.entity.ManaHorseEntity;
import dev.louis.zauber.item.ZauberItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.Instrument;
import net.minecraft.item.Instruments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class HorseRitual extends Ritual {
    private final BlockPos manaStorageBlockPos;
    private final HorseEntity horse;

    protected HorseRitual(World world, RitualStoneBlockEntity blockEntity, BlockPos manaStorageBlockPos, HorseEntity horse) {
        super(world, blockEntity);
        this.manaStorageBlockPos = manaStorageBlockPos;
        this.horse = horse;
    }

    @Override
    public void tick() {
        if(age % 5 == 0) {
            world.playSound(null, this.pos, SoundEvents.ENTITY_ARROW_HIT, SoundCategory.PLAYERS, 1, -4);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public boolean shouldStop() {
        return age > 40;
    }

    @Override
    public void finish() {
        //ItemSacrificerBlockEntity with a call goat horn.
        var manaStorageState = world.getBlockState(manaStorageBlockPos);
        int manaLevel = manaStorageState.get(ManaCauldron.MANA_LEVEL);
        if (horse.isAlive() && manaLevel >= 1 && HorseRitual.isCallGoatHorn(ritualStoneBlockEntity.getStoredStack())) {
            manaLevel--;
            ritualStoneBlockEntity.setStoredStack(ItemStack.EMPTY);
            if (manaLevel == 0) {
                world.setBlockState(manaStorageBlockPos, Blocks.CAULDRON.getDefaultState());
            } else {
                world.setBlockState(manaStorageBlockPos, manaStorageState.with(ManaCauldron.MANA_LEVEL, manaLevel));
            }
            horse.discard();
            world.playSound(null, this.pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1, 4);
            ItemStack itemStack = ZauberItems.SOUL_HORN.getDefaultStack();
            NbtCompound subNbt = itemStack.getOrCreateSubNbt("stored_entity");
            subNbt.putString("id", Registries.ENTITY_TYPE.getId(ManaHorseEntity.TYPE).toString());
            itemStack.setSubNbt("id", subNbt);
            world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, itemStack, 0, 0.3f, 0));
        }
    }

    @Override
    public float getVolume() {
        return 2;
    }

    @Override
    public Stream<Position> getConnections() {
        return Stream.of(
                this.manaStorageBlockPos.toCenterPos(),
                horse.getPos()
        );
    }

    @Override
    public float getPitch() {
        return -2;
    }

    public static Ritual tryStart(World world, RitualStoneBlockEntity ritualStoneBlockEntity) {
        var ritualStonePos = ritualStoneBlockEntity.getPos();
        var box = Box.of(ritualStonePos.toCenterPos(), 32, 20, 32);

        //var availableItemStacks = ritualStoneBlockEntity.getAvailableItemStacks();
        //if (availableItemStacks.count() != 1) return null;
        var ritualItemStack = ritualStoneBlockEntity.getStoredStack();

        var filledManaStorage = ritualStoneBlockEntity.getFilledManaStorages().findAny();
        var horse = getNearestEntity(HorseEntity.class, ritualStonePos, box, world);
        if(!HorseRitual.isCallGoatHorn(ritualItemStack) || filledManaStorage.isEmpty() || horse.isEmpty()) return null;
        return new HorseRitual(world, ritualStoneBlockEntity, filledManaStorage.get(), horse.get());
    }



    public static boolean isCallGoatHorn(@Nullable ItemStack itemStack) {
        if (itemStack == null || !itemStack.isOf(Items.GOAT_HORN)) return false;

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


    static <T extends Entity> Optional<T> getNearestEntity(Class<? extends T> entityClass, BlockPos pos, Box box, World world) {
        return getNearestEntity(entityClass, (entity) -> true, pos, box, world);
    }

    static <T extends Entity> Optional<T> getNearestEntity(Class<? extends T> entityClass, Predicate<T> predicate, BlockPos pos, Box box, World world) {
        var entities = world.getEntitiesByClass(entityClass, box, entityOfClass -> true);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double distance = -1.0;
        T entity = null;

        for(T entity2 : entities) {
            if (!predicate.test(entity2)) continue;
            double e = entity2.squaredDistanceTo(x, y, z);
            if (distance == -1.0 || e < distance) {
                distance = e;
                entity = entity2;
            }
        }

        return Optional.ofNullable(entity);
    }

}

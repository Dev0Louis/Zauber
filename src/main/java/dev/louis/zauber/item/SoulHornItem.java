package dev.louis.zauber.item;

import dev.louis.zauber.Zauber;
import dev.louis.zauber.entity.ManaHorseEntity;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SoulHornItem extends Item implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    private static final Identifier GOAT_HORN_ID = Identifier.tryParse("call_goat_horn");

    public SoulHornItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity serverUser && !user.getItemCooldownManager().isCoolingDown(this)) {
            if (!Zauber.isClientModded(serverUser)) return TypedActionResult.fail(user.getStackInHand(hand));

            playSound(world, serverUser);
            NbtComponent nbt = user.getStackInHand(hand).get(DataComponentTypes.ENTITY_DATA);
            if (nbt != null) {
                EntityType.fromNbt(nbt.getNbt()).ifPresent(entityType -> {
                    ManaHorseEntity manaHorseEntity = new ManaHorseEntity(world, serverUser);
                    manaHorseEntity.setPosition(serverUser.getPos());
                    world.spawnEntity(manaHorseEntity);
                    serverUser.getServerWorld().spawnParticles(ParticleTypes.SNOWFLAKE, serverUser.getX(), serverUser.getY(), serverUser.getZ(), 10, 1, 1, 1, 0.1);
                    serverUser.startRiding(manaHorseEntity);
                    user.getItemCooldownManager().set(ZauberItems.SOUL_HORN, 20);
                    user.getStackInHand(hand).decrement(1);
                });
            }
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if (Zauber.isClientModded(player)) return this;
        return Items.GOAT_HORN;
    }

    private static void playSound(World world, PlayerEntity player) {
        SoundEvent soundEvent = Registries.INSTRUMENT.get(GOAT_HORN_ID).soundEvent().value();
        world.playSoundFromEntity(null, player, soundEvent, SoundCategory.RECORDS, 16.0f, 1.0F);
        world.emitGameEvent(GameEvent.INSTRUMENT_PLAY, player.getPos(), GameEvent.Emitter.of(player));
    }


}

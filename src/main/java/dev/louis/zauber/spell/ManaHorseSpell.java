package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.entity.ManaHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ManaHorseSpell extends Spell {
    private static final Identifier GOAT_HORN_ID = Identifier.tryParse("call_goat_horn");
    private static final int LIFETIME = 100;

    public ManaHorseSpell(SpellType<?> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        if(this.getCaster() instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.getServerWorld().spawnParticles(ParticleTypes.SNOWFLAKE, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 10, 1, 1, 1, 0.1);
            var world = serverPlayer.getWorld();
            playSound(serverPlayer.getWorld(), serverPlayer);
            ManaHorseEntity manaHorseEntity = new ManaHorseEntity(world, this);
            manaHorseEntity.setPosition(serverPlayer.getPos());
            world.spawnEntity(manaHorseEntity);
            serverPlayer.startRiding(manaHorseEntity);
        }
    }

    @Override
    public boolean isCastable() {
        return super.isCastable() && !caster.hasVehicle();
    }

    @Override
    public boolean shouldStop() {
        return super.shouldStop() || !this.getCaster().isAlive();
    }

    @Override
    public int getDuration() {
        return 20 * 30;
    }

    private static void playSound(World world, PlayerEntity player) {
        SoundEvent soundEvent = Registries.INSTRUMENT.get(GOAT_HORN_ID).soundEvent().value();
        world.playSoundFromEntity(null, player, soundEvent, SoundCategory.RECORDS, 16.0f, 1.0F);
        world.emitGameEvent(GameEvent.INSTRUMENT_PLAY, player.getPos(), GameEvent.Emitter.of(player));
    }
}

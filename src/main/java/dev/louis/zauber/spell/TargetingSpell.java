package dev.louis.zauber.spell;

import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class TargetingSpell extends Spell {
    private Integer entityId;

    public TargetingSpell(SpellType<? extends Spell> spellType) {
        super(spellType);
    }

    @Override
    public void setCaster(PlayerEntity caster) {
        if(caster.getWorld().isClient()) {
           castedOn(Zauber.PLAYER_VIEWER_GETTER.getPlayerInView().orElse(null));
        }
        super.setCaster(caster);
    }

    @Override
    public void cast() {

    }

    public void castedOn(Entity castedOn) {
        if(castedOn == null)return;
        this.entityId = castedOn.getId();
    }

    @Nullable
    public Entity castedOn() {
        if(entityId == null)return null;
        return getCaster().getWorld().getEntityById(entityId);
    }

    @Override
    public PacketByteBuf writeBuf(PacketByteBuf buf) {
        super.writeBuf(buf);
        Optional<Integer> optionalInteger = castedOn() != null ? Optional.of(castedOn().getId()) : Optional.empty();
        buf.writeOptional(optionalInteger, PacketByteBuf::writeVarInt);
        return buf;
    }

    @Override
    public PacketByteBuf readBuf(PacketByteBuf buf) {
        super.readBuf(buf);
        Optional<Integer> o = buf.readOptional(PacketByteBuf::readVarInt);
        o.ifPresent(integer -> this.entityId = integer);
        return buf;
    }

    @Override
    public boolean isCastable() {
        return castedOn() != null && super.isCastable();
    }
}

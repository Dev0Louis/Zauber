package dev.louis.zauber.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;

public class ZauberParticleTypes {
    public static final DefaultParticleType MANA_RUNE = FabricParticleTypes.simple(false);
    public static final DefaultParticleType MANA_EXPLOSION = FabricParticleTypes.simple(true);
    public static final DefaultParticleType MANA_EXPLOSION_EMITTER = FabricParticleTypes.simple(true);
}

package dev.louis.zauber.spell.effect.type;

import dev.louis.nebula.api.spell.SpellEffectType;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.spell.effect.*;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SpellEffectTypes {
    public static final SpellEffectType<JuggernautSpellEffect> JUGGERNAUT = Registry.register(
            SpellEffectType.REGISTRY,
            Identifier.of(Zauber.MOD_ID, "juggernaut"),
            new SpellEffectType<>(JuggernautSpellEffect::new)
    );
    public static final SpellEffectType<RewindSpellEffect> REWIND = Registry.register(
            SpellEffectType.REGISTRY,
            Identifier.of(Zauber.MOD_ID, "rewind"),
            new SpellEffectType<>(RewindSpellEffect::new)
    );
    public static final SpellEffectType<SproutSpellEffect> SPROUT = Registry.register(
            SpellEffectType.REGISTRY,
            Identifier.of(Zauber.MOD_ID, "sprout"),
            new SpellEffectType<>(SproutSpellEffect::new)
    );
    public static final SpellEffectType<CloudJumpSpellEffect> CLOUD_JUMP = Registry.register(
            SpellEffectType.REGISTRY,
            Identifier.of(Zauber.MOD_ID, "cloud_jump"),
            new SpellEffectType<>(CloudJumpSpellEffect::new)
    );
    public static final SpellEffectType<DashSpellEffect> DASH = Registry.register(
            SpellEffectType.REGISTRY,
            Identifier.of(Zauber.MOD_ID, "dash"),
            new SpellEffectType<>(DashSpellEffect::new)
    );

    public static void init() {

    }
}

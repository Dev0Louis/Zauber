package dev.louis.zauber.spell.effect.type;

import dev.louis.nebula.api.spell.SpellEffectType;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.spell.effect.JuggernautSpellEffect;
import dev.louis.zauber.spell.effect.RewindSpellEffect;
import dev.louis.zauber.spell.effect.SproutSpellEffect;
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

    public static void init() {

    }
}

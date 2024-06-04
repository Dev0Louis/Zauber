package dev.louis.zauber.client.keybind;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.Zauber;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

;

public class SpellKeyBinding extends KeyBinding {
    private final SpellType<?> spellType;
    private final boolean hides;


    public SpellKeyBinding(SpellType<?> spellType, boolean hides) {
        super(
                "key." + Zauber.MOD_ID + ".spell." + spellType.getId().getPath(),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category." + Zauber.MOD_ID + ".spells"
        );
        this.spellType = spellType;
        this.hides = hides;
    }

    public boolean shouldShow() {
        if (!hides) return true;
        var player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        return player.getSpellManager().hasLearned(spellType);
    }
}

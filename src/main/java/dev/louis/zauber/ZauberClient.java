package dev.louis.zauber;

import dev.louis.nebula.api.spell.SpellType;
import dev.louis.zauber.config.ZauberConfig;
import dev.louis.zauber.keybind.SpellKeyBinding;
import dev.louis.zauber.keybind.SpellKeybindManager;
import dev.louis.zauber.networking.OptionSyncPacket;
import dev.louis.zauber.recipe.ZauberRecipes;
import dev.louis.zauber.spell.TargetingSpell;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

;

public class ZauberClient implements ClientModInitializer {
    private static SpellKeybindManager spellKeybindManager;

    @Override
    public void onInitializeClient() {
        ClientConfigurationNetworking.registerGlobalReceiver(OptionSyncPacket.TYPE, ZauberConfig::syncOptions);
        TargetingSpell.TargetedPlayerSelector.init();

        createSpellKeyBind(Zauber.Spells.ARROW, false);
        createSpellKeyBind(Zauber.Spells.PULL, false);
        createSpellKeyBind(Zauber.Spells.PUSH, false);
        createSpellKeyBind(Zauber.Spells.REWIND, false);
        createSpellKeyBind(Zauber.Spells.SUICIDE, false);
        createSpellKeyBind(Zauber.Spells.TELEPORT, false);
        createSpellKeyBind(Zauber.Spells.FIRE, false);
        createSpellKeyBind(Zauber.Spells.ICE, false);
        createSpellKeyBind(Zauber.Spells.SUPERNOVA, true);
        createSpellKeyBind(Zauber.Spells.JUGGERNAUT, true);
        createSpellKeyBind(Zauber.Spells.WIND_EXPEL, false);
        createSpellKeyBind(Zauber.Spells.SPROUT, false);
        createSpellKeyBind(Zauber.Spells.DASH, false);
        ZauberRecipes.initClient();
    }

    public static void createSpellKeyBind(SpellType<?> spellType, boolean hides){
        var keybind = KeyBindingHelper.registerKeyBinding(new SpellKeyBinding(spellType, hides));

        getSpellKeybindManager().setSpellKeyBinding(spellType, keybind);
    }

    public static boolean isPlayerTargetable(PlayerEntity targetedPlayer) {
        final var player = MinecraftClient.getInstance().player;
        return player != null && player.canSee(targetedPlayer) && player.isPartOfGame() && !(targetedPlayer.isCreative() || targetedPlayer.isSpectator() || targetedPlayer.isInvisibleTo(targetedPlayer) || player.isSpectator());
    }
    public static SpellKeybindManager getSpellKeybindManager() {
        if(spellKeybindManager != null)return spellKeybindManager;
        return (spellKeybindManager = new SpellKeybindManager());
    }

    public static Optional<PlayerEntity> getPlayerInView() {
        return TargetingSpell.TargetedPlayerSelector.getPlayerInView();
    }
}

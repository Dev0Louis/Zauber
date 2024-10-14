package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.client.ZauberClient;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.networking.play.c2s.ThrowBlockPayload;
import dev.louis.zauber.spell.type.PlayerSpellFactory;
import dev.louis.zauber.spell.type.SpellType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow @Final public GameOptions options;
    @Unique
    int spellCooldown = 0;

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void handelInputEventsForNebula(CallbackInfo ci) {
        if (spellCooldown > 0) {
            spellCooldown--;
            return;
        }

        for (SpellType<?> spellType : SpellType.REGISTRY) {
            var optionalKey = ZauberClient.getSpellKeybindManager().getKey(spellType);
            if (optionalKey.isPresent()) {
                var key = optionalKey.get();
                if (key.isPressed()) {
                    Spell<LivingEntity> spell = switch (spellType.factory()) {
                        case PlayerSpellFactory<?> simpleSpellFactory -> simpleSpellFactory.create();
                    };
                    SpellSource.of((LivingEntity) player).castSpell(spell);
                    this.resetSpellCooldown();
                    return;
                }
            }
        }
    }

    public void resetSpellCooldown() {
        spellCooldown = ConfigManager.getServerConfig().spellCooldown();
    }

    @WrapWithCondition(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V")
    )
    public boolean a(MinecraftClient client, boolean breaking) {
        if (client.player != null) {
            var stack = client.player.getStackInHand(client.player.getActiveHand());
            var hasStaff = stack.isOf(ZauberItems.STAFF);
            if (hasStaff && this.options.attackKey.isPressed()) {
                ClientPlayNetworking.send(ThrowBlockPayload.INSTANCE);
                return false;
            }
        }
        return true;
    }
}

package dev.louis.zauber.client.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellSource;
import dev.louis.zauber.client.ZauberClient;
import dev.louis.zauber.client.glisco.StencilFramebuffer;
import dev.louis.zauber.config.ConfigManager;
import dev.louis.zauber.item.ZauberItems;
import dev.louis.zauber.networking.play.c2s.ThrowTelekinedPayload;
import dev.louis.zauber.spell.type.PlayerSpellFactory;
import dev.louis.zauber.spell.type.SpellType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
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

    @WrapOperation(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z")
    )
    public boolean a(MinecraftClient client, Operation<Boolean> original, @Local(ordinal = 0, index = 1) LocalBooleanRef booleanRef   /*bl3*/) {
        if (client.player != null) {
            var stack = client.player.getStackInHand(client.player.getActiveHand());
            var hasStaff = stack.isOf(ZauberItems.STAFF);
            if (hasStaff && this.options.attackKey.isPressed()) {
                ClientPlayNetworking.send(ThrowTelekinedPayload.INSTANCE);
                client.player.swingHand(Hand.MAIN_HAND);
                return true;
            }
        }
        return original.call(client);
    }

    @Inject(
            method = "onResolutionChanged",
            at = @At("TAIL")
    )
    public void resizeStencilBuffer(CallbackInfo ci) {
            StencilFramebuffer.stencilFrameBuffer = new StencilFramebuffer(MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight());
    }
}

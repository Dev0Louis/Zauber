package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.louis.zauber.extension.EntityExtension;
import dev.louis.zauber.extension.PlayerEntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Entity.class)
public class EntityMixin implements EntityExtension {
    @Unique
    @Nullable
    private PlayerEntity telekineser;

    @Override
    public Optional<PlayerEntity> zauber$getTelekineser() {
        this.checkTelekinesis();
        return Optional.ofNullable(telekineser);
    }

    @Override
    public void zauber$setTelekineser(@NotNull PlayerEntity newTelekineser) {
        if (telekineser != null && telekineser != newTelekineser) {
            ((PlayerEntityExtension) telekineser).zauber$stopTelekinesis();
        }

        telekineser = newTelekineser;
    }

    @Override
    public void zauber$removeTelekinesisFrom(PlayerEntity player) {
        if (player == telekineser) {
            telekineser = null;
        }
    }


    @Override
    public boolean zauber$isTelekinesed() {
        this.checkTelekinesis();
        return telekineser != null;
    }

    private void checkTelekinesis() {
        if (telekineser == null) return;
        var isStillTelekinesed = ((PlayerEntityExtension) telekineser).zauber$getTelekined().map(entity -> entity == (Entity) (Object) EntityMixin.this).orElse(false);
        if (!isStillTelekinesed) telekineser = null;
    }

    @ModifyExpressionValue(
            method = "handleFallDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isIn(Lnet/minecraft/registry/tag/TagKey;)Z")
    )
    public boolean a(boolean isImmune) {
        return isImmune || this.zauber$isTelekinesed();
    }
}

package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import dev.emi.trinkets.api.TrinketsApi;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.duck.AttachableEntity;
import dev.louis.zauber.entity.TotemOfDarknessEntity;
import dev.louis.zauber.helper.SoundHelper;
import dev.louis.zauber.item.TotemOfDarknessItem;
import dev.louis.zauber.item.ZauberItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements AttachableEntity {
    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    public abstract ServerWorld getServerWorld();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique
    @Nullable
    private TotemOfDarknessEntity totemOfDarknessEntity;

    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    public void checkIfTotemOfDarknessIsEquippedAndIfNeededSpawnCompanionEntity(CallbackInfo ci) {
        var isDarkTotemPresent = TrinketsApi.getTrinketComponent(this).map(trinketComponent -> trinketComponent.isEquipped(ZauberItems.TOTEM_OF_DARKNESS)).orElse(false);
        if (isDarkTotemPresent) {
            if (TotemOfDarknessItem.isActive(this)) {
                if (totemOfDarknessEntity == null || totemOfDarknessEntity.isRemoved()) {
                    totemOfDarknessEntity = new TotemOfDarknessEntity(this.getWorld(), this);
                    totemOfDarknessEntity.setPosition(this.getPos());
                    this.getWorld().spawnEntity(totemOfDarknessEntity);
                    SoundHelper.playSound(
                            this.getServerWorld(),
                            this.getPos(),
                            SoundEvents.ENTITY_SHULKER_BULLET_HIT,
                            SoundCategory.AMBIENT,
                            1,
                            2
                    );
                }
            } else {
                if (totemOfDarknessEntity != null) {
                    SoundHelper.playSound(
                            this.getServerWorld(),
                            this.getPos(),
                            SoundEvents.ENTITY_SHULKER_BULLET_HIT,
                            SoundCategory.AMBIENT,
                            1,
                            1
                    );
                    totemOfDarknessEntity = null;
                }
            }
        } else {
            totemOfDarknessEntity = null;
        }
    }

    @Override
    public TotemOfDarknessEntity zauber$getTotemOfDarkness() {
        return totemOfDarknessEntity;
    }

    @ModifyExpressionValue(
            method = {"onDisconnect", "readCustomDataFromNbt"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSleeping()Z")
    )
    public boolean doNotWakeUpInTrappingBed(boolean isSleeping) {
        return isSleeping && !Zauber.isInTrappingBed(this);
    }
}

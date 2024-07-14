package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import dev.emi.trinkets.api.TrinketsApi;
import dev.louis.zauber.PlayerTotemData;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.duck.EntityWithFollowingEntities;
import dev.louis.zauber.entity.FollowingEntity;
import dev.louis.zauber.entity.TotemOfDarknessEntity;
import dev.louis.zauber.entity.TotemOfIceEntity;
import dev.louis.zauber.entity.TotemOfManaEntity;
import dev.louis.zauber.helper.SoundHelper;
import dev.louis.zauber.item.TotemOfDarknessItem;
import dev.louis.zauber.item.TotemOfIceItem;
import dev.louis.zauber.item.TotemOfManaItem;
import dev.louis.zauber.item.ZauberItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.louis.zauber.Zauber.ITEM_TO_TOTEM_DATA;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements EntityWithFollowingEntities {
    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    public abstract ServerWorld getServerWorld();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique
    @NotNull
    private List<FollowingEntity> followingEntities = new ArrayList<>();


    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    public void checkIfTotemOfDarknessIsEquippedAndIfNeededSpawnCompanionEntity(CallbackInfo ci) {
        //var isDarkTotemPresent = TrinketsApi.getTrinketComponent(this).map(trinketComponent -> trinketComponent.isEquipped(ZauberItems.TOTEM_OF_DARKNESS)).orElse(false);
        TrinketsApi.getTrinketComponent(this).ifPresent(component -> {
            ITEM_TO_TOTEM_DATA.forEach((item, playerTotemData) -> {
                var entityType = playerTotemData.entityType();
                var checker = playerTotemData.activityChecker();
                if (component.isEquipped(item) && checker.isActive(this)) {
                    if (followingEntities.stream().noneMatch(entity -> entity.getType().equals(entityType))) {
                        FollowingEntity followingEntity = entityType.create(this.getWorld());
                        followingEntity.setOwner(this);
                        followingEntity.setPosition(this.getPos());
                        followingEntities.add(followingEntity);
                        this.getWorld().spawnEntity(followingEntity);
                        followingEntity.onActivation();

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
                    if (followingEntities.removeIf(entity -> {
                        boolean remove = entity.getType().equals(entityType);
                        if (remove) entity.discard();
                        return remove;
                    })) {
                        SoundHelper.playSound(
                                this.getServerWorld(),
                                this.getPos(),
                                SoundEvents.ENTITY_SHULKER_BULLET_HIT,
                                SoundCategory.AMBIENT,
                                1,
                                1
                        );
                    }
                }
            });
        });

    }

    @Override
    public @NotNull List<FollowingEntity> zauber$getFollowingEntities() {
        return followingEntities;
    }

    @Inject(
            method = "wakeUp",
            at = @At("HEAD"),
            cancellable = true
    )
    public void noWakingUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo ci) {
        if (Zauber.isInTrappingBed(this)) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "trySleep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isDay()Z")
    )
    public boolean allowToSleepInTrappingBedDuringTheDay(boolean original, BlockPos pos) {
        return original && !Zauber.isTrappingBed(this.getWorld(), pos);
    }

    @ModifyExpressionValue(
            method = {"onDisconnect", "readCustomDataFromNbt"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSleeping()Z")
    )
    public boolean doNotWakeUpInTrappingBed(boolean isSleeping) {
        return isSleeping && !Zauber.isInTrappingBed(this);
    }
}

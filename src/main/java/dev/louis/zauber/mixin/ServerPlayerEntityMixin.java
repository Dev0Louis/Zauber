package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import dev.emi.trinkets.api.TrinketsApi;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.duck.AttachableEntity;
import dev.louis.zauber.entity.FollowingEntity;
import dev.louis.zauber.entity.TotemOfDarknessEntity;
import dev.louis.zauber.entity.TotemOfIceEntity;
import dev.louis.zauber.helper.SoundHelper;
import dev.louis.zauber.item.ZauberItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements AttachableEntity {
    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    public abstract ServerWorld getServerWorld();

    @Shadow @Final public ServerPlayerInteractionManager interactionManager;

    @Shadow private boolean inTeleportationState;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique
    @Nullable
    private List<FollowingEntity> followingEntity = new ArrayList<>();

    @Unique
    private static final HashMap<Item, EntityType<? extends FollowingEntity>> ITEM_TO_ENTITY_TYPE;

    static {
        ITEM_TO_ENTITY_TYPE = new HashMap<>();
        ITEM_TO_ENTITY_TYPE.put(ZauberItems.TOTEM_OF_DARKNESS, TotemOfDarknessEntity.TYPE);
        ITEM_TO_ENTITY_TYPE.put(ZauberItems.TOTEM_OF_ICE, TotemOfIceEntity.TYPE);
    }

    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    public void checkIfTotemOfDarknessIsEquippedAndIfNeededSpawnCompanionEntity(CallbackInfo ci) {
        //var isDarkTotemPresent = TrinketsApi.getTrinketComponent(this).map(trinketComponent -> trinketComponent.isEquipped(ZauberItems.TOTEM_OF_DARKNESS)).orElse(false);
        TrinketsApi.getTrinketComponent(this).ifPresent(component -> {
            ITEM_TO_ENTITY_TYPE.entrySet().stream().filter(entry -> component.isEquipped(entry.getKey())).map(Map.Entry::getValue);

        });


        if (isDarkTotemPresent) {
            if (followingEntity.isActive(this)) {
                if (followingEntity == null || followingEntity.isRemoved()) {
                    followingEntity = new TotemOfDarknessEntity(this.getWorld(), this);
                    followingEntity.setPosition(this.getPos());
                    this.getWorld().spawnEntity(followingEntity);
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
                if (followingEntity != null) {
                    SoundHelper.playSound(
                            this.getServerWorld(),
                            this.getPos(),
                            SoundEvents.ENTITY_SHULKER_BULLET_HIT,
                            SoundCategory.AMBIENT,
                            1,
                            1
                    );
                    followingEntity = null;
                }
            }
        } else {
            followingEntity = null;
        }
    }

    @Override
    public TotemOfDarknessEntity zauber$getTotemOfDarkness() {
        return followingEntity;
    }

    @ModifyExpressionValue(
            method = {"onDisconnect", "readCustomDataFromNbt"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSleeping()Z")
    )
    public boolean doNotWakeUpInTrappingBed(boolean isSleeping) {
        return isSleeping && !Zauber.isInTrappingBed(this);
    }
}

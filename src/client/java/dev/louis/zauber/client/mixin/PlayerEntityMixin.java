package dev.louis.zauber.client.mixin;

import dev.louis.zauber.client.ZauberClient;
import dev.louis.zauber.config.ConfigManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow public abstract void readCustomDataFromNbt(NbtCompound nbt);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isGlowing() {
        if(this.getWorld().isClient() && this.isTargetedPlayer()) return true;
        return super.isGlowing();
    }

    @Override
    public int getTeamColorValue() {
        if(this.getWorld().isClient() && this.isTargetedPlayer()) return ConfigManager.getClientConfig().targetingColor().getRGB();
        return super.getTeamColorValue();
    }

    private boolean isTargetedPlayer() {
        if(ZauberClient.isPlayerTargetable(ZauberClient.playerInView)) {
            return (Object) this == ZauberClient.playerInView;
        }
        return false;
    }
}

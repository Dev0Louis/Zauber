package dev.louis.zauber.mixin.client;

import dev.louis.zauber.ZauberClient;
import dev.louis.zauber.config.ZauberConfig;
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
        if(this.getWorld().isClient() && this.isTargetedPlayer()) return ZauberConfig.getTargetingColor().getRGB();
        return super.getTeamColorValue();
    }

    private boolean isTargetedPlayer() {
        var targetedPlayer = ZauberClient.getPlayerInView();
        if(targetedPlayer.isEmpty())return false;
        if(ZauberClient.isPlayerTargetable(targetedPlayer.get())) {
            return (Object) this == targetedPlayer.get();
        }
        return false;
    }
}

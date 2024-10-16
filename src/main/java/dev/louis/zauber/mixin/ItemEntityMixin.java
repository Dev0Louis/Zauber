package dev.louis.zauber.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.louis.zauber.item.HeartOfTheDarknessItem;
import dev.louis.zauber.tag.ZauberItemTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    public abstract ItemStack getStack();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z")
    )
    public boolean makeHeartDisappearInLight(boolean isEmpty) {
        if (!this.getWorld().isClient()) {
            boolean destroyedByLight = this.getStack().isIn(ZauberItemTags.DESTROYED_BY_LIGHT);
            boolean inLight = this.getWorld().getLightLevel(this.getBlockPos()) > HeartOfTheDarknessItem.MAX_BRIGHTNESS;
            if (destroyedByLight && inLight) {
                HeartOfTheDarknessItem.onDisappeared((ServerWorld) this.getWorld(), this.getPos());
            }
            return isEmpty || (destroyedByLight && inLight);
        }
        return isEmpty;
    }
}

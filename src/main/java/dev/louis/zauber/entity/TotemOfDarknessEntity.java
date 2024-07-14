package dev.louis.zauber.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.emi.trinkets.api.SlotReference;
import dev.louis.zauber.Zauber;
import dev.louis.zauber.item.TotemOfDarknessItem;
import dev.louis.zauber.item.ZauberItems;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TotemOfDarknessEntity extends FollowingEntity {
    public static final EntityType<TotemOfDarknessEntity> TYPE = FabricEntityTypeBuilder
            .<TotemOfDarknessEntity>create(SpawnGroup.MISC, TotemOfDarknessEntity::new)
            .build();

    public static final EntityAttributeModifier HALF =
            new EntityAttributeModifier(Identifier.of(Zauber.MOD_ID, "half"), -.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    public static final EntityAttributeModifier DOUBLE =
            new EntityAttributeModifier(Identifier.of(Zauber.MOD_ID, "double"), 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    public static Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> activeMap = Multimaps.newMultimap(Maps.newLinkedHashMap(), ArrayList::new);

    static {
        activeMap.put(EntityAttributes.GENERIC_MAX_HEALTH, HALF);
        activeMap.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, DOUBLE);
    }

    public TotemOfDarknessEntity(EntityType<?> type, World world) {
        super(type, world, ZauberItems.TOTEM_OF_DARKNESS.getDefaultStack());
    }

    @Override
    public boolean isActive(LivingEntity livingEntity) {
        return TotemOfDarknessItem.isActive(livingEntity);
    }


    @Override
    public void onActivation() {
        if (this.getWorld().isClient()) return;

        if (isActive(this.getOwner())) {
            this.getOwner().getAttributes().addTemporaryModifiers(this.getTotemModifiers());
        }
    }


    @Override
    public void tick() {
        if (this.getWorld().isClient()) return;

        super.tick();

        var owner = this.getOwner();
        if (isActive(owner)) {
            owner.getAttributes().addTemporaryModifiers(this.getTotemModifiers());
        } else {
            owner.getAttributes().removeModifiers(this.getTotemModifiers());
        }
    }

    @Override
    public void remove(RemovalReason removalReason) {
        if (this.getWorld().isClient()) return;

        this.getOwner().getAttributes().removeModifiers(this.getTotemModifiers());
    }


    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getTotemModifiers() {
        return activeMap;
    }
}

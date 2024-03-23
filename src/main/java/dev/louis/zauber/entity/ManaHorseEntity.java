package dev.louis.zauber.entity;

import dev.louis.nebula.api.NebulaPlayer;
import dev.louis.zauber.particle.ZauberParticleTypes;
import dev.louis.zauber.spell.ManaHorseSpell;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/*
TODO: Making it item bound, making it shake before it returns in the item. Better death sound and effect.
 */
public class ManaHorseEntity extends HorseEntity implements Ownable {
    public static final EntityType<ManaHorseEntity> TYPE =
            EntityType.Builder.<ManaHorseEntity>create(ManaHorseEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844F, 1.6F).maxTrackingRange(10).build("mana_horse");
    private ManaHorseSpell spell;

    public ManaHorseEntity(EntityType<? extends ManaHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    public ManaHorseEntity(World world, ManaHorseSpell spell) {
        super(TYPE, world);
        this.spell = spell;
    }

    @Override
    protected void initAttributes(Random random) {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(12);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.6);
        this.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(1.0);
    }

    public static DefaultAttributeContainer.Builder createBaseHorseAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.HORSE_JUMP_STRENGTH)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 12)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6);
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient()) {
            if (spell == null || spell.shouldStop()) {
                this.discard();
            }
        } else {
            spawnManaParticles();
        }
        super.tick();
    }

    public void spawnManaParticles() {
        var x = (this.random.nextFloat() - 0.5) * 2;
        var y = (this.random.nextFloat() - 0.5) * 2;
        var z = (this.random.nextFloat() - 0.5) * 2;
        var velocity = this.getVelocity();
        this.getWorld().addParticle(ZauberParticleTypes.MANA_RUNE, this.getX() + x, this.getEyeY() + y, this.getZ() + z, velocity.getX() * 1.1, velocity.getY(), velocity.getZ() * 1.1);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if(this.getWorld().isClient()) {
            this.getWorld().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 2.0, this.getZ(), 0.0, 0.0, 0.0);
        }

        super.onDeath(damageSource);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy()) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 3, 2);
        }

        if (spell != null) spell.interrupt();
        super.remove(reason);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDER_EYE_DEATH;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        this.discard();
    }

    public boolean willDisappearSoon() {
        //The Controller should always be equal to the caster.
        var controller = this.getControllingPassenger();
        return !(controller instanceof NebulaPlayer nebulaPlayer) || !nebulaPlayer.getManaManager().hasEnoughMana(4);
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return this.spell.getCaster();
    }

    @Override
    public HorseColor getVariant() {
        return HorseColor.WHITE;
    }

    @Override
    public boolean isSaddled() {
        return false;
    }

    @Override
    public boolean isTame() {
        return true;
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        var entity = this.getFirstPassenger();
        if(entity instanceof LivingEntity livingEntity) {
            //Check if livingEntity is owner.
            return livingEntity;
        }
        return null;
    }

    @Override
    public void equipHorseArmor(PlayerEntity player, ItemStack stack) {

    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isInvisibleTo(PlayerEntity player) {
        return false;
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Override
    public void setJumpStrength(int strength) {
        if (strength < 0) {
            strength = 0;
        } else {
            this.jumping = true;
            this.updateAnger();
        }

        if (strength >= 90) {
            this.jumpStrength = 1.0F;
        } else {
            this.jumpStrength = 0.4F + 0.4F * (float)strength / 90.0F;
        }
    }
}

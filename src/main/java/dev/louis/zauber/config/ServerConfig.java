package dev.louis.zauber.config;

import java.util.Objects;

public class ServerConfig {
    private int entityTargetingDistance = 24;
    private int blockTargetingDistance = 64;
    private int spellCooldown = 10;
    private float supernovaExplosionPower = 16.0F;
    private float dashVelocityMultiplier = 1.3F;
    private int dashSpellDuration = 5;
    private int sproutSpellDuration = 20 * 10;
    private int fireSpellDuration = 20;
    private int iceSpellDuration = 20;
    private int juggernautSpellDuration = 120 * 20;
    private int rewindSpellDuration = 6 * 20;
    private double windExpelSpellAcceleration = 0.1;
    private int windExpelSpellDuration = 20;
    private boolean convertOldNamespace = false;

    protected ServerConfig() {

    }

    public ServerConfig(
            int entityTargetingDistance,
            int spellCooldown,
            float supernovaExplosionPower,
            float dashVelocityMultiplier,
            int dashSpellDuration,
            int sproutSpellDuration,
            int fireSpellDuration,
            int iceSpellDuration,
            int juggernautSpellDuration,
            int rewindSpellDuration,
            double windExpelSpellAcceleration,
            int windExpelSpellDuration,
            boolean convertOldNamespace
    ) {
        this.entityTargetingDistance = entityTargetingDistance;
        this.spellCooldown = spellCooldown;
        this.supernovaExplosionPower = supernovaExplosionPower;
        this.dashVelocityMultiplier = dashVelocityMultiplier;
        this.dashSpellDuration = dashSpellDuration;
        this.sproutSpellDuration = sproutSpellDuration;
        this.fireSpellDuration = fireSpellDuration;
        this.iceSpellDuration = iceSpellDuration;
        this.juggernautSpellDuration = juggernautSpellDuration;
        this.rewindSpellDuration = rewindSpellDuration;
        this.windExpelSpellAcceleration = windExpelSpellAcceleration;
        this.windExpelSpellDuration = windExpelSpellDuration;
        this.convertOldNamespace = convertOldNamespace;
    }

    public void entityTargetingDistance(int entityTargetingDistance) {
        this.entityTargetingDistance = entityTargetingDistance;
    }

    public int entityTargetingDistance() {
        return entityTargetingDistance;
    }

    public void blockTargetingDistance(int blockTargetingDistance) {
        this.blockTargetingDistance = blockTargetingDistance;
    }

    public int blockTargetingDistance() {
        return blockTargetingDistance;
    }

    public void spellCooldown(int spellCooldown) {
        this.spellCooldown = spellCooldown;
    }

    public int spellCooldown() {
        return spellCooldown;
    }

    public void supernovaExplosionPower(float supernovaExplosionPower) {
        this.supernovaExplosionPower = supernovaExplosionPower;
    }

    public float supernovaExplosionPower() {
        return supernovaExplosionPower;
    }

    public void dashVelocityMultiplier(float dashVelocityMultiplier) {
        this.dashVelocityMultiplier = dashVelocityMultiplier;
    }

    public float dashVelocityMultiplier() {
        return dashVelocityMultiplier;
    }

    public void dashSpellDuration(int dashSpellDuration) {
        this.dashSpellDuration = dashSpellDuration;
    }

    public int dashSpellDuration() {
        return dashSpellDuration;
    }

    public void sproutSpellDuration(int sproutSpellDuration) {
        this.sproutSpellDuration = sproutSpellDuration;
    }

    public int sproutSpellDuration() {
        return sproutSpellDuration;
    }

    public void fireSpellDuration(int fireSpellDuration) {
        this.fireSpellDuration = fireSpellDuration;
    }

    public int fireSpellDuration() {
        return fireSpellDuration;
    }

    public void iceSpellDuration(int iceSpellDuration) {
        this.iceSpellDuration = iceSpellDuration;
    }

    public int iceSpellDuration() {
        return iceSpellDuration;
    }

    public void juggernautSpellDuration(int juggernautSpellDuration) {
        this.juggernautSpellDuration = juggernautSpellDuration;
    }

    public int juggernautSpellDuration() {
        return juggernautSpellDuration;
    }

    public void rewindSpellDuration(int rewindSpellDuration) {
        this.rewindSpellDuration = rewindSpellDuration;
    }

    public int rewindSpellDuration() {
        return rewindSpellDuration;
    }

    public void windExpelSpellAcceleration(double windExpelAcceleration) {
        this.windExpelSpellAcceleration = windExpelAcceleration;
    }

    public double windExpelSpellAcceleration() {
        return windExpelSpellAcceleration;
    }

    public void windExpelSpellDuration(int windExpelSpellDuration) {
        this.windExpelSpellDuration = windExpelSpellDuration;
    }

    public int windExpelSpellDuration() {
        return windExpelSpellDuration;
    }

    public void convertOldNamespace(boolean convertOldNamespace) {
        this.convertOldNamespace = convertOldNamespace;
    }

    public boolean convertOldNamespace() {
        return convertOldNamespace;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ServerConfig) obj;
        return this.entityTargetingDistance == that.entityTargetingDistance &&
                Float.floatToIntBits(this.supernovaExplosionPower) == Float.floatToIntBits(that.supernovaExplosionPower) &&
                this.dashSpellDuration == that.dashSpellDuration &&
                this.sproutSpellDuration == that.sproutSpellDuration &&
                this.fireSpellDuration == that.fireSpellDuration &&
                this.iceSpellDuration == that.iceSpellDuration &&
                this.juggernautSpellDuration == that.juggernautSpellDuration &&
                this.rewindSpellDuration == that.rewindSpellDuration &&
                this.windExpelSpellDuration == that.windExpelSpellDuration &&
                this.convertOldNamespace == that.convertOldNamespace;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityTargetingDistance, supernovaExplosionPower, dashSpellDuration, sproutSpellDuration, fireSpellDuration, iceSpellDuration, juggernautSpellDuration, rewindSpellDuration, windExpelSpellDuration, convertOldNamespace);
    }

    @Override
    public String toString() {
        return "ServerConfig[" +
                "entityTargetingDistance=" + entityTargetingDistance + ", " +
                "spellCooldown=" + spellCooldown + ", " +
                "supernovaExplosionPower=" + supernovaExplosionPower + ", " +
                "dashSpellDuration=" + dashSpellDuration + ", " +
                "sproutSpellDuration=" + sproutSpellDuration + ", " +
                "fireSpellDuration=" + fireSpellDuration + ", " +
                "iceSpellDuration=" + iceSpellDuration + ", " +
                "juggernautSpellDuration=" + juggernautSpellDuration + ", " +
                "rewindSpellDuration=" + rewindSpellDuration + ", " +
                "windExpelSpellDuration=" + windExpelSpellDuration + ", " +
                "convertOldNamespace=" + convertOldNamespace + ']';
    }
}

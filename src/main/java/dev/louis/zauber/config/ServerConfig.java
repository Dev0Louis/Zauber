package dev.louis.zauber.config;

import java.util.Objects;

public class ServerConfig {
    private int targetingDistance;
    private int spellCooldown;
    private float supernovaExplosionPower;
    private float dashVelocityMultiplier;
    private int dashSpellDuration;
    private int sproutSpellDuration;
    private int fireSpellDuration;
    private int iceSpellDuration;
    private int juggernautSpellDuration;
    private int rewindSpellDuration;
    private double windExpelSpellAcceleration;
    private int windExpelSpellDuration;
    private boolean convertOldNamespace;

    public ServerConfig(
            int targetingDistance,
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
        this.targetingDistance = targetingDistance;
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

    public ServerConfig() {
        this(
                20,
                10,
                16.0F,
                1.3F,
                5,
                20 * 10,
                20,
                20,
                120 * 20,
                6 * 20,
                0.1,
                20,
                false
        );
    }

    public void targetingDistance(int targetingDistance) {
        this.targetingDistance = targetingDistance;
    }

    public int targetingDistance() {
        return targetingDistance;
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
        return this.targetingDistance == that.targetingDistance &&
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
        return Objects.hash(targetingDistance, supernovaExplosionPower, dashSpellDuration, sproutSpellDuration, fireSpellDuration, iceSpellDuration, juggernautSpellDuration, rewindSpellDuration, windExpelSpellDuration, convertOldNamespace);
    }

    @Override
    public String toString() {
        return "ServerConfig[" +
                "targetingDistance=" + targetingDistance + ", " +
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

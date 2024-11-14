package dev.louis.zauber.client.extension;

public interface MinecraftClientExtension {
    default float zauber$getTelekinesisStartPogress() {
        throw new UnsupportedOperationException("BLOOP BLOOP. I am a Mixin method don't call me >:C");
    }
}

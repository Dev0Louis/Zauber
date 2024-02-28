package dev.louis.zauber.client.particle;

import dev.louis.zauber.particle.ZauberParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class ManaExplosionEmitterParticle extends NoRenderParticle {
	ManaExplosionEmitterParticle(ClientWorld clientWorld, double d, double e, double f) {
		super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
		this.maxAge = 8;
	}

	@Override
	public void tick() {
		for(int i = 0; i < 6; ++i) {
			double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			this.world.addParticle(ZauberParticleTypes.MANA_EXPLOSION, d, e, f, (float)this.age / (float)this.maxAge, 0.0, 0.0);
		}

		++this.age;
		if (this.age == this.maxAge) {
			this.markDead();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
			return new ManaExplosionEmitterParticle(clientWorld, x, y, z);
		}
	}
}

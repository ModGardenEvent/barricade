package net.modgarden.barricade.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.particle.AdvancedBarrierParticleOptions;

public class BarricadeParticleTypes {
	public static void registerAll() {
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, BarricadeMod.id("advanced_barrier"), AdvancedBarrierParticleOptions.Type.INSTANCE);
	}
}

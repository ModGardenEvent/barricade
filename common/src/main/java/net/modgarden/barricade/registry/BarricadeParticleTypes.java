package net.modgarden.barricade.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.particle.AdvancedBarrierParticleOptions;
import net.modgarden.barricade.registry.internal.RegistrationCallback;

public class BarricadeParticleTypes {

    public static void registerAll(RegistrationCallback<ParticleType<?>> callback) {
        callback.register(BuiltInRegistries.PARTICLE_TYPE, Barricade.asResource("advanced_barrier"), AdvancedBarrierParticleOptions.Type.INSTANCE);
    }
}

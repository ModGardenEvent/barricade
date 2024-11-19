package net.modgarden.barricade.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.data.AdvancedBarrier;

public class BarricadeRegistries {
    public static final ResourceKey<Registry<AdvancedBarrier>> ADVANCED_BARRIER = ResourceKey.createRegistryKey(Barricade.asResource("advanced_barrier"));
}

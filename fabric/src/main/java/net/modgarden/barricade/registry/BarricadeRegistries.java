package net.modgarden.barricade.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.data.BarricadeData;

public class BarricadeRegistries {
	public static final ResourceKey<Registry<BarricadeData>> BARRICADE = ResourceKey.createRegistryKey(BarricadeMod.id("barricade"));
}

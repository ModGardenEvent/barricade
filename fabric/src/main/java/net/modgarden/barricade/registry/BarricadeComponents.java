package net.modgarden.barricade.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.data.BarricadeData;
import net.modgarden.barricade.data.BlockedDirections;

public class BarricadeComponents {
	public static final DataComponentType<Holder<BarricadeData>> BARRICADE = DataComponentType.<Holder<BarricadeData>>builder()
			.persistent(BarricadeData.CODEC)
			.networkSynchronized(BarricadeData.STREAM_CODEC)
			.build();
	public static final DataComponentType<BlockedDirections> BLOCKED_DIRECTIONS = DataComponentType.<BlockedDirections>builder()
			.persistent(BlockedDirections.CODEC)
			.networkSynchronized(BlockedDirections.STREAM_CODEC)
			.build();

	public static void registerAll() {
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, BarricadeMod.id("barricade"), BARRICADE);
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, BarricadeMod.id("blocked_directions"), BLOCKED_DIRECTIONS);
	}
}

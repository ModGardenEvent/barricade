package net.modgarden.barricade.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;

import java.util.Set;

public class BarricadeBlockEntityTypes {
	public static final BlockEntityType<AdvancedBarrierBlockEntity> ADVANCED_BARRIER = new BlockEntityType<>(AdvancedBarrierBlockEntity::new, Set.of(BarricadeBlocks.ADVANCED_BARRIER.get()));

	public static void registerAll() {
		Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, BarricadeMod.id("advanced_barrier"), ADVANCED_BARRIER);
	}
}

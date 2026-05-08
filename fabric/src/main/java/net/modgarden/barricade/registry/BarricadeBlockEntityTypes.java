package net.modgarden.barricade.registry;

import static net.modgarden.barricade.BarricadeMod.id;

import net.modgarden.barricade.block.entity.BarricadeBlockEntity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

public final class BarricadeBlockEntityTypes {
	private BarricadeBlockEntityTypes() {
	}
	public static final BlockEntityType<BarricadeBlockEntity> BARRICADE =
			register("barricade", BarricadeBlockEntity::new, BarricadeBlocks.BARRICADE.get());

	public static void initialize() {
	}

	private static <T extends BlockEntity> BlockEntityType<T> register(
			String name,
			FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
			Block... blocks
	) {
		Identifier id = id(name);
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
	}
}

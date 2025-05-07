package net.modgarden.barricade.registry;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.*;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.silicate.api.SilicateRegistries;
import net.modgarden.silicate.api.condition.GameCondition;
import org.jetbrains.annotations.NotNull;

public class BarricadeBlocks {
	public static final AdvancedBarrierBlock ADVANCED_BARRIER = new AdvancedBarrierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());

	public static final DirectionalBarrierBlock DOWN_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.DOWN), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
	public static final DirectionalBarrierBlock UP_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.UP), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
	public static final DirectionalBarrierBlock SOUTH_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.SOUTH), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
	public static final DirectionalBarrierBlock NORTH_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.NORTH), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
	public static final DirectionalBarrierBlock EAST_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.EAST), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
	public static final DirectionalBarrierBlock WEST_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.WEST), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
	public static final DirectionalBarrierBlock HORIZONTAL_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
	public static final DirectionalBarrierBlock VERTICAL_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.UP, Direction.DOWN), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());

	// Predicate Barriers
	public static final PredicateBarrierBlock CREATIVE_ONLY_BARRIER = new PredicateBarrierBlock(
			BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
					.dynamicShape(),
			Barricade.asResource("barricade/icon/iron_sword"),
			ResourceKey.create(SilicateRegistries.CONDITION_TEMPLATE, Barricade.asResource("creative_only"))
	);

	public static final PredicateBarrierBlock PLAYER_BARRIER = new PredicateBarrierBlock(
			BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
					.dynamicShape(),
			Barricade.asResource("barricade/icon/steve"),
			ResourceKey.create(SilicateRegistries.CONDITION_TEMPLATE, Barricade.asResource("player"))
	);
	public static final PredicateBarrierBlock MOB_BARRIER = new PredicateBarrierBlock(
			BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
					.dynamicShape(),
			Barricade.asResource("barricade/icon/pig"),
			ResourceKey.create(SilicateRegistries.CONDITION_TEMPLATE, Barricade.asResource("mob"))
	);
	public static final PredicateBarrierBlock PASSIVE_BARRIER = new PredicateBarrierBlock(
			BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
					.dynamicShape(),
			Barricade.asResource("barricade/icon/parrot"),
			ResourceKey.create(SilicateRegistries.CONDITION_TEMPLATE, Barricade.asResource("passive"))
	);
	public static final PredicateBarrierBlock HOSTILE_BARRIER = new PredicateBarrierBlock(
			BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
					.dynamicShape(),
			Barricade.asResource("barricade/icon/creeper"),
			ResourceKey.create(SilicateRegistries.CONDITION_TEMPLATE, Barricade.asResource("hostile"))
	);

	// Predicate Levers
	public static final PredicateLeverBlock CREATIVE_ONLY_LEVER = new PredicateLeverBlock(
			leverProps(),
			Barricade.asResource("barricade/icon/iron_sword"),
			conditionTemplate("creative_only")
	);

	public static void registerAll() {
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("advanced_barrier"), ADVANCED_BARRIER);

		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("down_barrier"), DOWN_BARRIER);
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("up_barrier"), UP_BARRIER);
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("south_barrier"), SOUTH_BARRIER);
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("north_barrier"), NORTH_BARRIER);
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("east_barrier"), EAST_BARRIER);
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("west_barrier"), WEST_BARRIER);
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("horizontal_barrier"), HORIZONTAL_BARRIER);
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("vertical_barrier"), VERTICAL_BARRIER);

		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("player_barrier"), PLAYER_BARRIER);
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("mob_barrier"), MOB_BARRIER);
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("passive_barrier"), PASSIVE_BARRIER);
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("hostile_barrier"), HOSTILE_BARRIER);

		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource("creative_only_barrier"), CREATIVE_ONLY_BARRIER);

		register("creative_only_lever", CREATIVE_ONLY_LEVER);
	}

	private static void register(String name, Block block) {
		Registry.register(BuiltInRegistries.BLOCK, Barricade.asResource(name), block);
	}

	private static ResourceKey<GameCondition<?>> conditionTemplate(String name) {
		return ResourceKey.create(
				SilicateRegistries.CONDITION_TEMPLATE,
				Barricade.asResource(name)
		);
	}

	private static BlockBehaviour.@NotNull Properties leverProps() {
		return BlockBehaviour.Properties.ofFullCopy(Blocks.LEVER);
	}
}

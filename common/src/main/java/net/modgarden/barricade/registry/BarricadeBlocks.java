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
	public static final AdvancedBarrierBlock ADVANCED_BARRIER = new AdvancedBarrierBlock(barrierProps());

	public static final DirectionalBarrierBlock DOWN_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.DOWN), barrierProps());
	public static final DirectionalBarrierBlock UP_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.UP), barrierProps());
	public static final DirectionalBarrierBlock SOUTH_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.SOUTH), barrierProps());
	public static final DirectionalBarrierBlock NORTH_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.NORTH), barrierProps());
	public static final DirectionalBarrierBlock EAST_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.EAST), barrierProps());
	public static final DirectionalBarrierBlock WEST_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.WEST), barrierProps());
	public static final DirectionalBarrierBlock HORIZONTAL_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH), barrierProps());
	public static final DirectionalBarrierBlock VERTICAL_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.UP, Direction.DOWN), barrierProps());

	// Predicate Barriers
	public static final PredicateBarrierBlock CREATIVE_ONLY_BARRIER = new PredicateBarrierBlock(
			barrierProps(),
			Barricade.asResource("barricade/icon/iron_sword"),
			conditionTemplate("creative_only")
	);

	public static final PredicateBarrierBlock PLAYER_BARRIER = new PredicateBarrierBlock(
			barrierProps(),
			Barricade.asResource("barricade/icon/steve"),
			conditionTemplate("player")
	);
	public static final PredicateBarrierBlock MOB_BARRIER = new PredicateBarrierBlock(
			barrierProps(),
			Barricade.asResource("barricade/icon/pig"),
			conditionTemplate("mob")
	);
	public static final PredicateBarrierBlock PASSIVE_BARRIER = new PredicateBarrierBlock(
			barrierProps(),
			Barricade.asResource("barricade/icon/parrot"),
			conditionTemplate("passive")
	);
	public static final PredicateBarrierBlock HOSTILE_BARRIER = new PredicateBarrierBlock(
			barrierProps(),
			Barricade.asResource("barricade/icon/creeper"),
			conditionTemplate("hostile")
	);

	// Predicate Levers
	public static final PredicateLeverBlock CREATIVE_ONLY_LEVER = new PredicateLeverBlock(
			leverProps(),
			Barricade.asResource("barricade/icon/iron_sword"),
			conditionTemplate("creative_only")
	);

	// Predicate Buttons
	public static final PredicateButtonBlock CREATIVE_ONLY_BUTTON = new PredicateButtonBlock(
			buttonProps(),
			Barricade.asResource("barricade/icon/iron_sword"),
			conditionTemplate("creative_only")
	);

	public static void registerAll() {
		register("advanced_barrier", ADVANCED_BARRIER);

		register("down_barrier", DOWN_BARRIER);
		register("up_barrier", UP_BARRIER);
		register("south_barrier", SOUTH_BARRIER);
		register("north_barrier", NORTH_BARRIER);
		register("east_barrier", EAST_BARRIER);
		register("west_barrier", WEST_BARRIER);
		register("horizontal_barrier", HORIZONTAL_BARRIER);
		register("vertical_barrier", VERTICAL_BARRIER);

		register("player_barrier", PLAYER_BARRIER);
		register("mob_barrier", MOB_BARRIER);
		register("passive_barrier", PASSIVE_BARRIER);
		register("hostile_barrier", HOSTILE_BARRIER);

		register("creative_only_barrier", CREATIVE_ONLY_BARRIER);

		register("creative_only_lever", CREATIVE_ONLY_LEVER);

		register("creative_only_button", CREATIVE_ONLY_BUTTON);
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

	private static BlockBehaviour.@NotNull Properties barrierProps() {
		return BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
				.dynamicShape();
	}

	private static BlockBehaviour.@NotNull Properties leverProps() {
		return BlockBehaviour.Properties.ofFullCopy(Blocks.LEVER);
	}

	private static BlockBehaviour.@NotNull Properties buttonProps() {
		return BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BUTTON);
	}
}

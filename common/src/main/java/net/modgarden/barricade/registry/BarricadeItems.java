package net.modgarden.barricade.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.block.PredicateBarrierBlock;
import net.modgarden.barricade.block.PredicateButtonBlock;
import net.modgarden.barricade.block.PredicateLeverBlock;
import net.modgarden.barricade.item.AdvancedBarrierBlockItem;
import net.modgarden.barricade.item.DirectionalBarrierBlockItem;
import net.modgarden.barricade.item.EntityCheckBarrierBlockItem;
import net.modgarden.barricade.item.OperatorBlockItem;
import org.jetbrains.annotations.NotNull;

public class BarricadeItems {
	public static final OperatorBlockItem<Block> ADVANCED_BARRIER = new AdvancedBarrierBlockItem(BarricadeBlocks.ADVANCED_BARRIER, barrierProps());

	public static final OperatorBlockItem<DirectionalBarrierBlock> HORIZONTAL_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.HORIZONTAL_BARRIER, barrierProps());
	public static final OperatorBlockItem<DirectionalBarrierBlock> VERTICAL_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.VERTICAL_BARRIER, barrierProps());
	public static final OperatorBlockItem<DirectionalBarrierBlock> DOWN_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.DOWN_BARRIER, barrierProps());
	public static final OperatorBlockItem<DirectionalBarrierBlock> UP_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.UP_BARRIER, barrierProps());
	public static final OperatorBlockItem<DirectionalBarrierBlock> SOUTH_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.SOUTH_BARRIER, barrierProps());
	public static final OperatorBlockItem<DirectionalBarrierBlock> NORTH_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.NORTH_BARRIER, barrierProps());
	public static final OperatorBlockItem<DirectionalBarrierBlock> EAST_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.EAST_BARRIER, barrierProps());
	public static final OperatorBlockItem<DirectionalBarrierBlock> WEST_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.WEST_BARRIER, barrierProps());

	// Predicate Barriers
	public static final OperatorBlockItem<PredicateBarrierBlock> PLAYER_BARRIER = new EntityCheckBarrierBlockItem<>(BarricadeBlocks.PLAYER_BARRIER, barrierProps());
	public static final OperatorBlockItem<PredicateBarrierBlock> MOB_BARRIER = new EntityCheckBarrierBlockItem<>(BarricadeBlocks.MOB_BARRIER, barrierProps());
	public static final OperatorBlockItem<PredicateBarrierBlock> PASSIVE_BARRIER = new EntityCheckBarrierBlockItem<>(BarricadeBlocks.PASSIVE_BARRIER, barrierProps());
	public static final OperatorBlockItem<PredicateBarrierBlock> HOSTILE_BARRIER = new EntityCheckBarrierBlockItem<>(BarricadeBlocks.HOSTILE_BARRIER, barrierProps());

	public static final OperatorBlockItem<PredicateBarrierBlock> CREATIVE_ONLY_BARRIER = new OperatorBlockItem<>(
			BarricadeBlocks.CREATIVE_ONLY_BARRIER,
			barrierProps()
	);

	// Predicate Levers
	public static final OperatorBlockItem<PredicateLeverBlock> CREATIVE_ONLY_LEVER = new OperatorBlockItem<>(
			BarricadeBlocks.CREATIVE_ONLY_LEVER,
			barrierProps()
	);

	// Predicate Buttons
	public static final OperatorBlockItem<PredicateButtonBlock> CREATIVE_ONLY_BUTTON = new OperatorBlockItem<>(
			BarricadeBlocks.CREATIVE_ONLY_BUTTON,
			barrierProps()
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

	private static void register(String name, BlockItem blockItem) {
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource(name), blockItem);
	}

	private static Item.@NotNull Properties barrierProps() {
		return new Item.Properties().rarity(Rarity.EPIC);
	}
}

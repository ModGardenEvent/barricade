package net.modgarden.barricade.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.item.BarricadeBlockItem;
import net.modgarden.barricade.item.DirectionalBarrierBlockItem;
import net.modgarden.barricade.item.EntityCheckBarrierBlockItem;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BarricadeItems {
	private static final RegistryContext<Item> CONTEXT = new RegistryContext<>(
			BuiltInRegistries.ITEM,
			BarricadeMod.MOD_ID
	);

	public static final Supplier<BlockItem> BARRICADE = CONTEXT.defer(
			"barricade",
			withProperties(BarricadeBlocks.BARRICADE, BarricadeBlockItem::new)
	);

	public static final Supplier<BlockItem> HORIZONTAL_BARRIER = CONTEXT.defer(
			"horizontal_barrier",
			withProperties(BarricadeBlocks.HORIZONTAL_BARRIER, DirectionalBarrierBlockItem::new)
	);
	public static final Supplier<BlockItem> VERTICAL_BARRIER = CONTEXT.defer(
			"vertical_barrier",
			withProperties(BarricadeBlocks.VERTICAL_BARRIER, DirectionalBarrierBlockItem::new)
	);
	public static final Supplier<BlockItem> DOWN_BARRIER = CONTEXT.defer(
			"down_barrier",
			withProperties(BarricadeBlocks.DOWN_BARRIER, DirectionalBarrierBlockItem::new)
	);
	public static final Supplier<BlockItem> UP_BARRIER = CONTEXT.defer(
			"up_barrier",
			withProperties(BarricadeBlocks.UP_BARRIER, DirectionalBarrierBlockItem::new)
	);
	public static final Supplier<BlockItem> SOUTH_BARRIER = CONTEXT.defer(
			"south_barrier",
			withProperties(BarricadeBlocks.SOUTH_BARRIER, DirectionalBarrierBlockItem::new)
	);
	public static final Supplier<BlockItem> NORTH_BARRIER = CONTEXT.defer(
			"north_barrier",
			withProperties(BarricadeBlocks.NORTH_BARRIER, DirectionalBarrierBlockItem::new)
	);
	public static final Supplier<BlockItem> EAST_BARRIER = CONTEXT.defer(
			"east_barrier",
			withProperties(BarricadeBlocks.EAST_BARRIER, DirectionalBarrierBlockItem::new)
	);
	public static final Supplier<BlockItem> WEST_BARRIER = CONTEXT.defer(
			"west_barrier",
			withProperties(BarricadeBlocks.WEST_BARRIER, DirectionalBarrierBlockItem::new)
	);

	// Predicate Barriers
	public static final Supplier<BlockItem> PLAYER_BARRIER = CONTEXT.defer(
			"player_barrier",
			withProperties(BarricadeBlocks.PLAYER_BARRIER, EntityCheckBarrierBlockItem::new)
	);
	public static final Supplier<BlockItem> MOB_BARRIER = CONTEXT.defer(
			"mob_barrier",
			withProperties(BarricadeBlocks.MOB_BARRIER, EntityCheckBarrierBlockItem::new)
	);
	public static final Supplier<BlockItem> PASSIVE_BARRIER = CONTEXT.defer(
			"passive_barrier",
			withProperties(BarricadeBlocks.PASSIVE_BARRIER, EntityCheckBarrierBlockItem::new)
	);
	public static final Supplier<BlockItem> HOSTILE_BARRIER = CONTEXT.defer(
			"hostile_barrier",
			withProperties(BarricadeBlocks.HOSTILE_BARRIER, EntityCheckBarrierBlockItem::new)
	);

	public static final Supplier<BlockItem> CREATIVE_ONLY_BARRIER = CONTEXT.defer(
			"creative_only_barrier",
			withProperties(BarricadeBlocks.CREATIVE_ONLY_BARRIER, BlockItem::new)
	);

	public static void registerAll() {
		CONTEXT.register();
	}

	@SuppressWarnings("unchecked") // ResourceKey<T> is always ResourceKey<Item>
	private static <T extends Item, B extends Block> Function<ResourceKey<T>, T> withProperties(Supplier<B> block, BiFunction<Block, Item.Properties, T> callback) {
		return key -> callback.apply(block.get(), new Item.Properties().rarity(Rarity.EPIC).setId((ResourceKey<Item>) key).useBlockDescriptionPrefix());
	}
}

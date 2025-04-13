package net.modgarden.barricade.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.item.AdvancedBarrierBlockItem;
import net.modgarden.barricade.item.DirectionalBarrierBlockItem;
import net.modgarden.barricade.item.EntityCheckBarrierBlockItem;
import org.jetbrains.annotations.NotNull;

public class BarricadeItems {
	public static final BlockItem ADVANCED_BARRIER = new AdvancedBarrierBlockItem(BarricadeBlocks.ADVANCED_BARRIER, barrierProps());

	public static final BlockItem HORIZONTAL_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.HORIZONTAL_BARRIER, barrierProps());
	public static final BlockItem VERTICAL_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.VERTICAL_BARRIER, barrierProps());
	public static final BlockItem DOWN_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.DOWN_BARRIER, barrierProps());
	public static final BlockItem UP_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.UP_BARRIER, barrierProps());
	public static final BlockItem SOUTH_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.SOUTH_BARRIER, barrierProps());
	public static final BlockItem NORTH_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.NORTH_BARRIER, barrierProps());
	public static final BlockItem EAST_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.EAST_BARRIER, barrierProps());
	public static final BlockItem WEST_BARRIER = new DirectionalBarrierBlockItem(BarricadeBlocks.WEST_BARRIER, barrierProps());

	public static final BlockItem PLAYER_BARRIER = new EntityCheckBarrierBlockItem(BarricadeBlocks.PLAYER_BARRIER, barrierProps());
	public static final BlockItem MOB_BARRIER = new EntityCheckBarrierBlockItem(BarricadeBlocks.MOB_BARRIER, barrierProps());
	public static final BlockItem PASSIVE_BARRIER = new EntityCheckBarrierBlockItem(BarricadeBlocks.PASSIVE_BARRIER, barrierProps());
	public static final BlockItem HOSTILE_BARRIER = new EntityCheckBarrierBlockItem(BarricadeBlocks.HOSTILE_BARRIER, barrierProps());

	// Predicate Barriers
	public static final BlockItem CREATIVE_ONLY_BARRIER = new BlockItem(
			BarricadeBlocks.CREATIVE_ONLY_BARRIER,
			barrierProps()
	);

	public static void registerAll() {
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("advanced_barrier"), ADVANCED_BARRIER);

		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("down_barrier"), DOWN_BARRIER);
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("up_barrier"), UP_BARRIER);
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("south_barrier"), SOUTH_BARRIER);
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("north_barrier"), NORTH_BARRIER);
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("east_barrier"), EAST_BARRIER);
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("west_barrier"), WEST_BARRIER);
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("horizontal_barrier"), HORIZONTAL_BARRIER);
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("vertical_barrier"), VERTICAL_BARRIER);

		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("player_barrier"), PLAYER_BARRIER);
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("mob_barrier"), MOB_BARRIER);
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("passive_barrier"), PASSIVE_BARRIER);
		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("hostile_barrier"), HOSTILE_BARRIER);

		Registry.register(BuiltInRegistries.ITEM, Barricade.asResource("creative_only_barrier"), CREATIVE_ONLY_BARRIER);
	}

	private static Item.@NotNull Properties barrierProps() {
		return new Item.Properties().rarity(Rarity.EPIC);
	}
}
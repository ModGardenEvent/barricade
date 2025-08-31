package net.modgarden.barricade.registry;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.AdvancedBarrierBlock;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.block.PredicateBarrierBlock;
import net.modgarden.barricade.block.StaticBarrierBlock;
import net.modgarden.silicate.api.SilicateRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public class BarricadeBlocks {
	private static final RegistryContext<Block> CONTEXT = new RegistryContext<>(
			BuiltInRegistries.BLOCK,
			Barricade.MOD_ID
	);

	public static Supplier<AdvancedBarrierBlock> ADVANCED_BARRIER = CONTEXT.defer(
			"advanced_barrier",
			withProperties(AdvancedBarrierBlock::new)
	);

	public static final Supplier<DirectionalBarrierBlock> DOWN_BARRIER = CONTEXT.defer(
			"down_barrier",
			withProperties(properties -> new DirectionalBarrierBlock(properties, Direction.DOWN))
	);
	public static final Supplier<DirectionalBarrierBlock> UP_BARRIER = CONTEXT.defer(
			"up_barrier",
			withProperties(properties -> new DirectionalBarrierBlock(properties, Direction.UP))
	);
	public static final Supplier<DirectionalBarrierBlock> SOUTH_BARRIER = CONTEXT.defer(
			"south_barrier",
			withProperties(properties -> new DirectionalBarrierBlock(properties, Direction.SOUTH))
	);
	public static final Supplier<DirectionalBarrierBlock> NORTH_BARRIER = CONTEXT.defer(
			"north_barrier",
			withProperties(properties -> new DirectionalBarrierBlock(properties, Direction.NORTH))
	);
	public static final Supplier<DirectionalBarrierBlock> EAST_BARRIER = CONTEXT.defer(
			"east_barrier",
			withProperties(properties -> new DirectionalBarrierBlock(properties, Direction.EAST))
	);
	public static final Supplier<DirectionalBarrierBlock> WEST_BARRIER = CONTEXT.defer(
			"west_barrier",
			withProperties(properties -> new DirectionalBarrierBlock(properties, Direction.WEST))
	);
	public static final Supplier<DirectionalBarrierBlock> HORIZONTAL_BARRIER = CONTEXT.defer(
			"horizontal_barrier",
			withProperties(properties -> new DirectionalBarrierBlock(properties, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST))
	);
	public static final Supplier<DirectionalBarrierBlock> VERTICAL_BARRIER = CONTEXT.defer(
			"vertical_barrier",
			withProperties(properties -> new DirectionalBarrierBlock(properties, Direction.UP, Direction.DOWN))
	);

	// Predicate Barriers
	public static final Supplier<PredicateBarrierBlock> CREATIVE_ONLY_BARRIER = CONTEXT.defer(
			"creative_only_barrier",
			withProperties(properties -> new PredicateBarrierBlock(
					properties,
					Barricade.asResource("barricade/icon/iron_sword"),
					ResourceKey.create(SilicateRegistries.CONDITION_TEMPLATE, Barricade.asResource("creative_only"))
			))
	);

	public static final Supplier<PredicateBarrierBlock> PLAYER_BARRIER = CONTEXT.defer(
			"player_barrier",
			withProperties(properties -> new PredicateBarrierBlock(
					properties,
					Barricade.asResource("barricade/icon/steve"),
					ResourceKey.create(SilicateRegistries.CONDITION_TEMPLATE, Barricade.asResource("player"))
			))
	);
	public static final Supplier<PredicateBarrierBlock> MOB_BARRIER = CONTEXT.defer(
			"mob_barrier",
			withProperties(properties -> new PredicateBarrierBlock(
					properties,
					Barricade.asResource("barricade/icon/pig"),
					ResourceKey.create(SilicateRegistries.CONDITION_TEMPLATE, Barricade.asResource("mob"))
			))
	);
	public static final Supplier<PredicateBarrierBlock> PASSIVE_BARRIER = CONTEXT.defer(
			"passive_barrier",
			withProperties(properties -> new PredicateBarrierBlock(
					properties,
					Barricade.asResource("barricade/icon/parrot"),
					ResourceKey.create(SilicateRegistries.CONDITION_TEMPLATE, Barricade.asResource("passive"))
			))
	);
	public static final Supplier<PredicateBarrierBlock> HOSTILE_BARRIER = CONTEXT.defer(
			"hostile_barrier",
			withProperties(properties -> new PredicateBarrierBlock(
					properties,
					Barricade.asResource("barricade/icon/creeper"),
					ResourceKey.create(SilicateRegistries.CONDITION_TEMPLATE, Barricade.asResource("hostile"))
			))
	);

	public static void registerAll() {
		CONTEXT.register();
	}

	@SuppressWarnings("unchecked") // ResourceKey<T> is always ResourceKey<Block>
	private static <T extends Block> Function<ResourceKey<T>, T> withProperties(Function<BlockBehaviour.Properties, T> callback) {
		return key -> {
			T block = callback.apply(BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape().setId((ResourceKey<Block>) key));
			if (block instanceof StaticBarrierBlock barrierBlock) {
				StaticBarrierBlock.BARRIERS.put(key.location(), barrierBlock);
			}
			return block;
		};
	}
}

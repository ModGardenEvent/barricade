package net.modgarden.barricade.registry;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.block.BarricadeBlock;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.block.PredicateBarrierBlock;
import net.modgarden.barricade.block.StaticBarrierBlock;
import lgbt.greenhouse.silicate.api.SilicateRegistries;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class BarricadeBlocks {
	private static final RegistryContext<Block> CONTEXT = new RegistryContext<>(
			BuiltInRegistries.BLOCK,
			BarricadeMod.MOD_ID
	);

	public static Supplier<BarricadeBlock> BARRICADE = CONTEXT.defer(
			"barricade",
			withProperties(BarricadeBlock::new)
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
					BarricadeMod.id("iron_sword"),
					ResourceKey.create(SilicateRegistries.CONDITION, BarricadeMod.id("creative_only"))
			))
	);

	public static final Supplier<PredicateBarrierBlock> PLAYER_BARRIER = CONTEXT.defer(
			"player_barrier",
			withProperties(properties -> new PredicateBarrierBlock(
					properties,
					BarricadeMod.id("steve"),
					ResourceKey.create(SilicateRegistries.CONDITION, BarricadeMod.id("player"))
			))
	);
	public static final Supplier<PredicateBarrierBlock> MOB_BARRIER = CONTEXT.defer(
			"mob_barrier",
			withProperties(properties -> new PredicateBarrierBlock(
					properties,
					BarricadeMod.id("pig"),
					ResourceKey.create(SilicateRegistries.CONDITION, BarricadeMod.id("mob"))
			))
	);
	public static final Supplier<PredicateBarrierBlock> PASSIVE_BARRIER = CONTEXT.defer(
			"passive_barrier",
			withProperties(properties -> new PredicateBarrierBlock(
					properties,
					BarricadeMod.id("parrot"),
					ResourceKey.create(SilicateRegistries.CONDITION, BarricadeMod.id("passive"))
			))
	);
	public static final Supplier<PredicateBarrierBlock> HOSTILE_BARRIER = CONTEXT.defer(
			"hostile_barrier",
			withProperties(properties -> new PredicateBarrierBlock(
					properties,
					BarricadeMod.id("creeper"),
					ResourceKey.create(SilicateRegistries.CONDITION, BarricadeMod.id("hostile"))
			))
	);

	public static final Set<Supplier<? extends StaticBarrierBlock>> BLOCKS = Set.of(
			BarricadeBlocks.HORIZONTAL_BARRIER,
			BarricadeBlocks.VERTICAL_BARRIER,
			BarricadeBlocks.CREATIVE_ONLY_BARRIER,
			BarricadeBlocks.PLAYER_BARRIER
	);

	public static void registerAll() {
		CONTEXT.register();
	}

	@SuppressWarnings("unchecked") // ResourceKey<T> is always ResourceKey<Block>
	private static <T extends Block> Function<ResourceKey<T>, T> withProperties(Function<BlockBehaviour.Properties, T> callback) {
		return key -> {
			T block = callback.apply(BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape().setId((ResourceKey<Block>) key));
			if (block instanceof StaticBarrierBlock barrierBlock) {
				StaticBarrierBlock.BARRIERS.put(key.identifier(), barrierBlock);
			}
			return block;
		};
	}
}

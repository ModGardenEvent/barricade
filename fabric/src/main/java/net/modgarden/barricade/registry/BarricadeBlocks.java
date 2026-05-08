package net.modgarden.barricade.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.block.BarricadeBlock;
import net.modgarden.barricade.block.StaticBarrierBlock;

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

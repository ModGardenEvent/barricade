package net.modgarden.barricade.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.item.BarricadeBlockItem;

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

	public static void registerAll() {
		CONTEXT.register();
	}

	@SuppressWarnings("unchecked") // ResourceKey<T> is always ResourceKey<Item>
	private static <T extends Item, B extends Block> Function<ResourceKey<T>, T> withProperties(Supplier<B> block, BiFunction<Block, Item.Properties, T> callback) {
		return key -> callback.apply(block.get(), new Item.Properties().rarity(Rarity.EPIC).setId((ResourceKey<Item>) key).useBlockDescriptionPrefix());
	}
}

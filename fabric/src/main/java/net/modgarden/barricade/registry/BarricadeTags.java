package net.modgarden.barricade.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.BarricadeMod;

public class BarricadeTags {
	public static class BlockTags {
		public static final TagKey<Block> BARRIERS = TagKey.create(Registries.BLOCK, BarricadeMod.id("barriers"));
	}

	public static class ItemTags {
		public static final TagKey<Item> BARRIERS = TagKey.create(Registries.ITEM, BarricadeMod.id("barriers"));
	}
}

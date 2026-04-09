package net.modgarden.barricade.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.BarricadeMod;

public class BarricadeTags {
	public static class BlockTags {
		public static final TagKey<Block> BARRIERS = TagKey.create(Registries.BLOCK, BarricadeMod.id("barriers"));
		public static final TagKey<Block> DIRECTIONAL_BARRIERS = TagKey.create(Registries.BLOCK, BarricadeMod.id("directional_barriers"));
		public static final TagKey<Block> ENTITY_BARRIERS = TagKey.create(Registries.BLOCK, BarricadeMod.id("entity_barriers"));
		public static final TagKey<Block> PREDICATE_BARRIERS = TagKey.create(Registries.BLOCK, BarricadeMod.id("predicate_barriers"));
	}

	public static class EntityTags {
		public static final TagKey<EntityType<?>> BLOCKED_BY_MOB_BARRIER = TagKey.create(Registries.ENTITY_TYPE, BarricadeMod.id("blocked_by_mob_barrier"));
		public static final TagKey<EntityType<?>> BLOCKED_BY_PASSIVE_BARRIER = TagKey.create(Registries.ENTITY_TYPE, BarricadeMod.id("blocked_by_passive_barrier"));
		public static final TagKey<EntityType<?>> BLOCKED_BY_HOSTILE_BARRIER = TagKey.create(Registries.ENTITY_TYPE, BarricadeMod.id("blocked_by_hostile_barrier"));
	}

	public static class ItemTags {
		public static final TagKey<Item> BARRIERS = TagKey.create(Registries.ITEM, BarricadeMod.id("barriers"));
	}
}

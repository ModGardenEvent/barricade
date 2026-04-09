package net.modgarden.barricade.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.modgarden.barricade.block.StaticBarrierBlock;
import net.modgarden.barricade.mixin.Accessor_TagsProvider;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeItems;
import net.modgarden.barricade.registry.BarricadeTags;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class BarricadeDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(ModelProvider::new);
		BlockTagProvider blockTagProvider = pack.addProvider(BlockTagProvider::new);
		pack.addProvider(EntityTypeTagsProvider::new);
		pack.addProvider((output, registries) -> new ItemTagProvider(output, registries, blockTagProvider));
	}

	private static class ModelProvider extends FabricModelProvider {
		public ModelProvider(FabricPackOutput output) {
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerators) {
			for (var barrier : StaticBarrierBlock.BARRIERS.values()) {
				blockStateModelGenerators.createAirLikeBlock(barrier, Items.BARRIER);
			}

			blockStateModelGenerators.createAirLikeBlock(BarricadeBlocks.ADVANCED_BARRIER.get(), Items.BARRIER);
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerators) {
			for (var barrier : StaticBarrierBlock.BARRIERS.values()) {
				itemModelGenerators.generateFlatItem(barrier.asItem(), ModelTemplates.FLAT_ITEM);
			}

			generateNoBarrierItem(itemModelGenerators, BarricadeItems.ADVANCED_BARRIER.get());
		}

		private static void generateNoBarrierItem(
				ItemModelGenerators generators,
				Item item
		) {
			generators.itemModelOutput.accept(item, ItemModelUtils.plainModel(createNoBarrierModel(generators, item)));
		}

		private static Identifier createNoBarrierModel(
				ItemModelGenerators generators,
				Item item
		) {
			return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(item), generators.modelOutput);
		}
	}

	private static class BlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
		public BlockTagProvider(
				FabricPackOutput output,
				CompletableFuture<HolderLookup.Provider> registriesFuture
		) {
			super(output, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider lookup) {
			getOrCreateTagBuilder(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS, this).add(BarricadeBlocks.ADVANCED_BARRIER.get(), BarricadeBlocks.DOWN_BARRIER.get(), BarricadeBlocks.UP_BARRIER.get(), BarricadeBlocks.NORTH_BARRIER.get(), BarricadeBlocks.SOUTH_BARRIER.get(), BarricadeBlocks.EAST_BARRIER.get(), BarricadeBlocks.WEST_BARRIER.get(), BarricadeBlocks.HORIZONTAL_BARRIER.get(), BarricadeBlocks.VERTICAL_BARRIER.get());
			getOrCreateTagBuilder(BarricadeTags.BlockTags.ENTITY_BARRIERS, this).add(BarricadeBlocks.ADVANCED_BARRIER.get(), BarricadeBlocks.PLAYER_BARRIER.get(), BarricadeBlocks.MOB_BARRIER.get(), BarricadeBlocks.PASSIVE_BARRIER.get(), BarricadeBlocks.HOSTILE_BARRIER.get());
			getOrCreateTagBuilder(BarricadeTags.BlockTags.PREDICATE_BARRIERS, this).forceAddTag(BarricadeTags.BlockTags.ENTITY_BARRIERS).add(BarricadeBlocks.CREATIVE_ONLY_BARRIER.get());
			getOrCreateTagBuilder(BarricadeTags.BlockTags.BARRIERS, this).add(Blocks.BARRIER).forceAddTag(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS).forceAddTag(BarricadeTags.BlockTags.PREDICATE_BARRIERS);
			getOrCreateTagBuilder(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS, this).forceAddTag(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS).forceAddTag(BarricadeTags.BlockTags.PREDICATE_BARRIERS);
			getOrCreateTagBuilder(BlockTags.CANNOT_SUPPORT_SNOW_LAYER, this).forceAddTag(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS).forceAddTag(BarricadeTags.BlockTags.PREDICATE_BARRIERS);
			getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE, this).forceAddTag(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS).forceAddTag(BarricadeTags.BlockTags.PREDICATE_BARRIERS);
			getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE, this).forceAddTag(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS).forceAddTag(BarricadeTags.BlockTags.PREDICATE_BARRIERS);
		}
	}

	private static class EntityTypeTagsProvider extends FabricTagsProvider.EntityTypeTagsProvider {

		public EntityTypeTagsProvider(
				FabricPackOutput output,
				CompletableFuture<HolderLookup.Provider> registriesFuture
		) {
			super(output, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			getOrCreateTagBuilder(BarricadeTags.EntityTags.BLOCKED_BY_MOB_BARRIER, this).forceAddTag(BarricadeTags.EntityTags.BLOCKED_BY_HOSTILE_BARRIER).forceAddTag(BarricadeTags.EntityTags.BLOCKED_BY_PASSIVE_BARRIER);
			getOrCreateTagBuilder(BarricadeTags.EntityTags.BLOCKED_BY_PASSIVE_BARRIER, this).add(
					EntityType.ALLAY, EntityType.ARMADILLO, EntityType.AXOLOTL, EntityType.BAT, EntityType.BEE, EntityType.CAMEL, EntityType.CAT, EntityType.CHICKEN, EntityType.COD, EntityType.COW, EntityType.DOLPHIN, EntityType.DONKEY, EntityType.FOX, EntityType.FROG, EntityType.GLOW_SQUID, EntityType.GOAT, EntityType.HORSE, EntityType.IRON_GOLEM, EntityType.LLAMA, EntityType.MOOSHROOM, EntityType.MULE, EntityType.OCELOT, EntityType.PANDA, EntityType.PARROT, EntityType.PIG, EntityType.POLAR_BEAR, EntityType.PUFFERFISH, EntityType.RABBIT, EntityType.SALMON, EntityType.SHEEP, EntityType.SKELETON_HORSE, EntityType.SNIFFER, EntityType.SNOW_GOLEM, EntityType.SQUID, EntityType.STRIDER, EntityType.TADPOLE, EntityType.TRADER_LLAMA, EntityType.TROPICAL_FISH, EntityType.TURTLE, EntityType.VILLAGER, EntityType.WANDERING_TRADER, EntityType.WOLF, EntityType.ZOMBIE_HORSE
			);
			getOrCreateTagBuilder(BarricadeTags.EntityTags.BLOCKED_BY_HOSTILE_BARRIER, this).add(EntityType.BLAZE, EntityType.BOGGED, EntityType.BREEZE, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.GHAST, EntityType.GIANT, EntityType.GUARDIAN, EntityType.HOGLIN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.PHANTOM, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.SHULKER, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.STRAY, EntityType.VEX, EntityType.WARDEN, EntityType.WITCH, EntityType.WITHER, EntityType.WITHER_SKELETON, EntityType.ZOGLIN, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER).forceAddTag(EntityTypeTags.RAIDERS);
		}
	}

	private static class ItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
		public ItemTagProvider(
				FabricPackOutput output,
				CompletableFuture<HolderLookup.Provider> completableFuture,
				@Nullable FabricTagsProvider.BlockTagsProvider blockTagsProvider
		) {
			super(output, completableFuture, blockTagsProvider);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup) {
			getOrCreateTagBuilder(BarricadeTags.ItemTags.BARRIERS, this).add(Items.BARRIER).add(BarricadeItems.ADVANCED_BARRIER.get()).add(StaticBarrierBlock.BARRIERS.values().stream().map(Block::asItem).toArray(Item[]::new));
		}
	}

	private static <V> TagBuilder2<V> getOrCreateTagBuilder(
			TagKey<V> tagKey,
			TagsProvider<V> provider
	) {
		@SuppressWarnings({"unchecked", "rawtypes"}) // fuck you Mojang
		Registry<V> registry = BuiltInRegistries.REGISTRY.getValueOrThrow((ResourceKey) tagKey.registry());
		return new TagBuilder2<>(registry, ((Accessor_TagsProvider) provider).barricade$getOrCreateRawBuilder(tagKey));
	}

	private record TagBuilder2<V>(Registry<V> registry, TagBuilder tagBuilder) {
		@SafeVarargs
		public final TagBuilder2<V> add(V... elements) {
			for (V element : elements) {
				this.tagBuilder.addElement(Objects.requireNonNull(this.registry.getKey(element)));
			}

			return this;
		}

		public TagBuilder2<V> forceAddTag(TagKey<V> tagKey) {
			this.tagBuilder.addOptionalTag(tagKey.location());
			return this;
		}
	}
}

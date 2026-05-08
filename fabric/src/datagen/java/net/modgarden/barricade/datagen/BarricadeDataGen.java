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
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

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
			blockStateModelGenerators.createAirLikeBlock(BarricadeBlocks.BARRICADE.get(), Items.BARRIER);
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerators) {
			generateNoBarrierItem(itemModelGenerators, BarricadeItems.BARRICADE.get());
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
			getOrCreateTagBuilder(BarricadeTags.BlockTags.BARRIERS, this).add(Blocks.BARRIER).add(BarricadeBlocks.BARRICADE.get());
			getOrCreateTagBuilder(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS, this).forceAddTag(BarricadeTags.BlockTags.BARRIERS);
			getOrCreateTagBuilder(BlockTags.CANNOT_SUPPORT_SNOW_LAYER, this).forceAddTag(BarricadeTags.BlockTags.BARRIERS);
			getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE, this).forceAddTag(BarricadeTags.BlockTags.BARRIERS);
			getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE, this).forceAddTag(BarricadeTags.BlockTags.BARRIERS);
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
			getOrCreateTagBuilder(BarricadeTags.ItemTags.BARRIERS, this).add(Items.BARRIER).add(BarricadeItems.BARRICADE.get());
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

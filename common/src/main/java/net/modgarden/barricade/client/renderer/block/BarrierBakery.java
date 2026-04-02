package net.modgarden.barricade.client.renderer.block;

import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.StaticBarrierBlock;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.data.BlockedDirections;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BarrierBakery {
	public static final BlockElementFace.UVs UVS = new BlockElementFace.UVs(0.0F, 0.0F, 16.0F, 16.0F);
	public static final UnbakedModel UNKNOWN_UNBAKED_MODEL = createBlockModel(AdvancedBarrier.UNKNOWN);

	private BarrierBakery() {}

	public static void bakeModels(Registry<AdvancedBarrier> advancedBarrierRegistry, Map<Identifier, BlockStateModel> modelMap) {
		Map<Identifier, UnbakedModel> unbakedModelMap = advancedBarrierRegistry.keySet().stream()
				.collect(Collectors.toMap(
						Function.identity(),
						id -> createBlockModel(advancedBarrierRegistry.get(id).orElseThrow().value())
				));
		ModelDiscovery modelDiscovery = new ModelDiscovery(unbakedModelMap, UNKNOWN_UNBAKED_MODEL);
		unbakedModelMap.forEach(modelDiscovery::addSpecialModel);
		Baker baker = new Baker(modelDiscovery);
		for (AdvancedBarrier data : advancedBarrierRegistry) {
			Identifier id = advancedBarrierRegistry.getKey(data);
			modelMap.put(id, createModel(id, modelDiscovery, baker));
		}
	}

	public static void bakeModels(Map<Identifier, StaticBarrierBlock> barriers, Map<Identifier, BlockStateModel> modelMap) {
		Map<Identifier, UnbakedModel> unbakedModelMap = barriers.keySet().stream()
				.collect(Collectors.toMap(
						Function.identity(),
						id -> createBlockModel(barriers.get(id))
				));
		ModelDiscovery modelDiscovery = new ModelDiscovery(unbakedModelMap, UNKNOWN_UNBAKED_MODEL);
		unbakedModelMap.forEach(modelDiscovery::addSpecialModel);
		Baker baker = new Baker(modelDiscovery);
		for (Identifier id : barriers.keySet()) {
			modelMap.put(id, createModel(id, modelDiscovery, baker));
		}
	}

	public static void bakeModels(Identifier id, Identifier texture, Map<Identifier, BlockStateModel> modelMap) {
		Map<Identifier, UnbakedModel> unbakedModelMap = Map.of(id, createBlockModel(texture));
		ModelDiscovery modelDiscovery = new ModelDiscovery(unbakedModelMap, UNKNOWN_UNBAKED_MODEL);
		unbakedModelMap.forEach(modelDiscovery::addSpecialModel);
		Baker baker = new Baker(modelDiscovery);
		modelMap.put(id, createModel(id, modelDiscovery, baker));
	}

	private static @NotNull BlockElement createBlockElement(String texture, List<Direction> faces) {
		return new BlockElement(
				new Vector3f(0.0f, 0.0f, 0.0f),
				new Vector3f(16.0f, 16.0f, 16.0f),
				faces.stream().collect(Collectors.toMap(
						key -> key,
						direction -> new BlockElementFace(direction, -1, texture, UVS, Quadrant.R0)
				))
		);
	}

	private static @NotNull SimpleUnbakedGeometry createUnbakedGeometry(List<Direction> directions, boolean hasIcon) {
		List<BlockElement> elements;
		if (hasIcon) {
			elements = List.of(
					createBlockElement("icon", directions),
					createBlockElement("barrier", directions)
			);
		} else {
			elements = List.of(createBlockElement("barrier", directions));
		}
		return new SimpleUnbakedGeometry(elements);
	}

	@SuppressWarnings("deprecation") // LOCATION_BLOCKS has no alternative
	private static @NotNull BlockModel createBlockModel(AdvancedBarrier data) {
		return new BlockModel(
				createUnbakedGeometry(data.directions().directions().stream().toList(), true),
				UnbakedModel.GuiLight.SIDE,
				false,
				ItemTransforms.NO_TRANSFORMS,
				new TextureSlots.Data.Builder()
						.addTexture(
								"icon",
								new Material(
										TextureAtlas.LOCATION_BLOCKS,
										data.icon().orElse(AdvancedBarrier.UNKNOWN_ICON)
								)
						)
						.addTexture(
								"barrier",
								new Material(TextureAtlas.LOCATION_BLOCKS, Barricade.asResource("block/barrier"))
						)
						.build(),
				null
		);
	}

	@SuppressWarnings("deprecation") // LOCATION_BLOCKS has no alternative
	private static @NotNull BlockModel createBlockModel(StaticBarrierBlock barrier) {
		TextureSlots.Data.Builder slots = new TextureSlots.Data.Builder();
		if (barrier.getIcon() != null) {
			slots.addTexture(
					"icon",
					new Material(TextureAtlas.LOCATION_BLOCKS, barrier.getIcon())
			);
		}
		slots.addTexture(
				"barrier",
				new Material(TextureAtlas.LOCATION_BLOCKS, Barricade.asResource("block/barrier"))
		);
		return new BlockModel(
				createUnbakedGeometry(
						barrier.getBlockedDirections().directions().stream().toList(),
						barrier.getIcon() != null
				),
				UnbakedModel.GuiLight.SIDE,
				false,
				ItemTransforms.NO_TRANSFORMS,
				slots.build(),
				null
		);
	}

	@SuppressWarnings("deprecation") // LOCATION_BLOCKS has no alternative
	private static @NotNull BlockModel createBlockModel(Identifier texture) {
		TextureSlots.Data.Builder slots = new TextureSlots.Data.Builder()
				.addTexture(
						"barrier",
						new Material(TextureAtlas.LOCATION_BLOCKS, texture)
				);
		return new BlockModel(
				createUnbakedGeometry(
						BlockedDirections.all().directions().stream().toList(),
						false
				),
				UnbakedModel.GuiLight.SIDE,
				false,
				ItemTransforms.NO_TRANSFORMS,
				slots.build(),
				null
		);
	}

	private static BlockStateModel createModel(Identifier id, ModelDiscovery modelDiscovery, Baker baker) {
		BlockModelPart modelPart = createModelPart(id, modelDiscovery, baker);
		return new SingleVariant(modelPart);
	}

	private static @NotNull BlockModelPart createModelPart(Identifier id, ModelDiscovery modelDiscovery, Baker baker) {
		QuadCollection quadCollection = modelDiscovery.resolve().get(id)
				.bakeTopGeometry(
						modelDiscovery.resolve().get(id).getTopTextureSlots(),
						baker,
						BlockModelRotation.X0_Y0
				);
		@SuppressWarnings("deprecation") // No alternative for LOCATION_BLOCKS
		BlockModelPart modelPart = new SimpleModelWrapper(
				quadCollection,
				false,
				new Material(TextureAtlas.LOCATION_BLOCKS, Identifier.withDefaultNamespace("block/barrier")).sprite()
		);
		return modelPart;
	}

	public static class Baker implements ModelBaker {
		private final Map<Identifier, ResolvedModel> models;
		private final Map<ModelBaker.SharedOperationKey<Object>, Object> operationCache = new ConcurrentHashMap<>();
		private final Function<SharedOperationKey<Object>, Object> cacheComputeFunction = key -> key.compute(this);

		private Baker(ModelDiscovery modelDiscovery) {
			this.models = modelDiscovery.resolve();
		}

		@Override
		public @NotNull ResolvedModel getModel(@NotNull Identifier modelLocation) {
			return this.models.get(modelLocation);
		}

		@Override
		public @NotNull SpriteGetter sprites() {
			return new SpriteGetter() {
				@SuppressWarnings("deprecation") // No alternative to LOCATION_BLOCKS
				private final TextureAtlasSprite missingSprite = new Material(TextureAtlas.LOCATION_BLOCKS, AdvancedBarrier.UNKNOWN_ICON).sprite();

				@Override
				public @NotNull TextureAtlasSprite get(
						@NotNull Material material,
						@NotNull ModelDebugName debugName
				) {
					return material.sprite();
				}

				@Override
				public @NotNull TextureAtlasSprite reportMissingReference(
						@NotNull String name,
						@NotNull ModelDebugName debugName
				) {
					return missingSprite;
				}
			};
		}

		@SuppressWarnings("unchecked") // I don't know what this is used for, so the types are unknown.
		@Override
		public <T> @NotNull T compute(@NotNull SharedOperationKey<T> key) {
			return (T) this.operationCache.computeIfAbsent((SharedOperationKey<Object>) key, this.cacheComputeFunction);
		}
	}
}

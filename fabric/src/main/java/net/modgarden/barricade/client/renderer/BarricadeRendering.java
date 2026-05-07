package net.modgarden.barricade.client.renderer;

import static net.modgarden.barricade.BarricadeMod.id;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.modgarden.barricade.client.model.BarricadeBlockStateModel;
import net.modgarden.barricade.client.renderer.item.BarricadeItemTextures;
import net.modgarden.barricade.data.BarricadeData;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperUnbakedItemModel;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.model.MeshQuadCollection;

public final class BarricadeRendering {
	private BarricadeRendering() {
	}

	public static void initialize() {
		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.registerBlockStateResolver(BarricadeBlocks.BARRICADE.get(), context -> {
				BarricadeBlockStateModel.Unbaked model = new BarricadeBlockStateModel.Unbaked();

				for (BlockState state : context.block().getStateDefinition().getPossibleStates()) {
					context.setModel(state, model);
				}
			});
			pluginContext.modifyItemModelBeforeBake().register((model, context1) -> {
				if (!context1.itemId().equals(id("barricade"))) return model;

				return new SpecialModelWrapper.Unbaked(
						Identifier.withDefaultNamespace("item/generated"), Optional.empty(),
						new SpecialModelRenderer.Unbaked<BarricadeData>() {
							@Override
							public @NonNull SpecialModelRenderer<BarricadeData> bake(SpecialModelRenderer.BakingContext context) {
								MaterialBaker materials = context1.bakingContext().blockModelBaker().materials();
								Material.Baked barrier = getBarrier(BarricadeItemTextures.BARRIER_TEXTURE, materials);
								Material.Baked noBarrier = getBarrier(BarricadeItemTextures.NO_BARRIER_TEXTURE, materials);
								Material.Baked upBarrier = getBarrier(BarricadeItemTextures.DIRECTION_UP_TEXTURE, materials);
								Material.Baked downBarrier = getBarrier(BarricadeItemTextures.DIRECTION_DOWN_TEXTURE, materials);
								Material.Baked northBarrier = getBarrier(BarricadeItemTextures.DIRECTION_NORTH_TEXTURE, materials);
								Material.Baked southBarrier = getBarrier(BarricadeItemTextures.DIRECTION_SOUTH_TEXTURE, materials);
								Material.Baked westBarrier = getBarrier(BarricadeItemTextures.DIRECTION_WEST_TEXTURE, materials);
								Material.Baked eastBarrier = getBarrier(BarricadeItemTextures.DIRECTION_EAST_TEXTURE, materials);
								Map<Direction, Material.Baked> directionMaterialMap = Map.of(
										Direction.UP, upBarrier,
										Direction.DOWN, downBarrier,
										Direction.NORTH, northBarrier,
										Direction.SOUTH, southBarrier,
										Direction.WEST, westBarrier,
										Direction.EAST, eastBarrier
								);
								return new SpecialModelRenderer<>() {
									@Override
									public void submit(
											@Nullable BarricadeData argument,
											PoseStack poseStack,
											SubmitNodeCollector submitNodeCollector,
											int lightCoords,
											int overlayCoords,
											boolean hasFoil,
											int outlineColor
									) {
										if (argument == null) {
											argument = BarricadeData.UNKNOWN;
										}

										Material.Baked higher = barrier;
										Material.Baked lower = null;

										if (argument.directions().directions().size() == 1) {
											higher = noBarrier;
											lower = directionMaterialMap.get(argument.directions().directions().stream().findAny().orElseThrow());
										}

										if (argument.icon().isPresent()) {
											// TODO: reduce object churn?
											TextureAtlasSprite iconSprite = Minecraft.getInstance()
													.getAtlasManager()
													.getAtlasOrThrow(QuadAtlas.BLOCK.getId())
													.getSprite(argument.icon().get());
											lower = new Material.Baked(iconSprite, false);
										}

										MutableMesh mesh = Renderer.get().mutableMesh();
										QuadEmitter emitter = mesh.emitter();

										emitter
												.square(Direction.SOUTH, 0, 0, 1, 1, 0.5f)
												.materialBake(higher, MutableQuadView.BAKE_LOCK_UV)
												.emit()
												.square(Direction.NORTH, 0, 0, 1, 1, 0.5f)
												.materialBake(higher, MutableQuadView.BAKE_LOCK_UV)
												.emit();

										if (lower != null) {
											emitter
													.square(Direction.SOUTH, 0, 0, 1, 1, 0.5f)
													.materialBake(lower, MutableQuadView.BAKE_LOCK_UV)
													.emit()
													.square(Direction.NORTH, 0, 0, 1, 1, 0.5f)
													.materialBake(lower, MutableQuadView.BAKE_LOCK_UV)
													.emit();
										}

										submitNodeCollector.order(0).submitItem(poseStack, ItemDisplayContext.NONE, lightCoords, overlayCoords, outlineColor, new int[0], List.of(), mesh, hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE);
									}

									@Override
									public void getExtents(Consumer<Vector3fc> output) {
									}

									@Override
									public @NonNull BarricadeData extractArgument(ItemStack stack) {
										Holder<BarricadeData> holder = stack.getOrDefault(BarricadeComponents.BARRICADE, BarricadeData.DEFAULT_HOLDER);

										if (!holder.isBound()) {
											return BarricadeData.DEFAULT;
										}

										return holder.value();
									}
								};
							}

							@Override
							public MapCodec<? extends SpecialModelRenderer.Unbaked<BarricadeData>> type() {
								return MapCodec.unit(this);
							}
						});
			});
		});
	}

	private static Material.@NonNull Baked getBarrier(Identifier identifier, MaterialBaker materials) {
		return materials.get(new Material(identifier), identifier::toString);
	}
}

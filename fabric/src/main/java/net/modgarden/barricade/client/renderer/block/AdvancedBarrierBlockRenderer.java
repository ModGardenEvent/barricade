package net.modgarden.barricade.client.renderer.block;

import static net.modgarden.barricade.BarricadeMod.id;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeRegistries;

import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;

public class AdvancedBarrierBlockRenderer extends BakedBlockEntityRenderer<AdvancedBarrierBlockEntity, AdvancedBarrierBlockRenderer.RenderState> {
	private static final RandomSource RANDOM = RandomSource.createThreadLocalInstance(BarricadeMod.MEANING_OF_EVERYTHING);

	public AdvancedBarrierBlockRenderer(BlockEntityRendererProvider.Context context) {
		super(context, BarricadeBlockEntityTypes.ADVANCED_BARRIER);
		BakedRegion.registerRegionBaker(Baker::new);
	}

	@Override
	public void extractBakedRenderState(
			AdvancedBarrierBlockEntity blockEntity,
			RenderState state,
			float partialTicks,
			Vec3 cameraPosition,
			ModelFeatureRenderer.CrumblingOverlay breakProgress
	) {
		super.extractBakedRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

		if (blockEntity.hasLevel()) {
			state.model = BarrierBakery2.getModel(Objects.requireNonNull(blockEntity.getLevel().registryAccess().getOrThrow(BarricadeRegistries.ADVANCED_BARRIER).value().getKey(blockEntity.getData())));
		} else {
			state.model = BarrierBakery2.getModel(id("passive_barrier"));
		}
	}

	@Override
	public boolean bakeAndSubmit(
			RenderState state,
			BakedRegion.CachedMultiBufferSource bufferSource
	) {
		QuadEmitter emitter = Renderer.get().quadEmitter(quad ->
				quad.buffer(OverlayTexture.NO_OVERLAY, bufferSource.getBuffer()));
		state.model.emitQuads(emitter, BlockAndTintGetter.EMPTY, state.blockPos, Blocks.AIR.defaultBlockState(), RANDOM, _ -> false); // FIXME: culling
		return true;
	}

	@Override
	public boolean shouldBake(AdvancedBarrierBlockEntity blockEntity) {
		return true;
	}

	public static void reloadModels(Minecraft mc) {
		if (mc.level == null) return; // FIXME
		Registry<AdvancedBarrier> registry = mc.level.registryAccess().lookupOrThrow(BarricadeRegistries.ADVANCED_BARRIER);
		Map<Identifier, AdvancedBarrier> barricades = new HashMap<>();

		for (Map.Entry<ResourceKey<AdvancedBarrier>, AdvancedBarrier> entry : registry.entrySet())
		{
			barricades.put(entry.getKey().identifier(), entry.getValue());
		}

		BarrierBakery2.bake(barricades);
	}

	@Override
	public RenderState createRenderState() {
		return new RenderState();
	}

	public class Baker extends BakedBlockEntityRenderer<AdvancedBarrierBlockEntity, RenderState>.Baker implements BarrierRegionBaker {
		public Baker(BakedRegion.BakedRegionPos pos) {
			super(pos, id("advanced_barrier"));
		}
	}

	public static class RenderState extends BlockEntityRenderState {
		public BlockStateModel model;
	}
}

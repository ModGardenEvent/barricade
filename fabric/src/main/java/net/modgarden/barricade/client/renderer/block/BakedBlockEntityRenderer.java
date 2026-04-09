package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

/**
 * Inspired by Glowcase's <a href="https://github.com/ModFest/glowcase/blob/b3681b46158733e632af22ea6c53afc342d9cf2e/src/main/java/dev/hephaestus/glowcase/client/render/block/entity/BakedBlockEntityRenderer.java">BakedBlockEntityRenderer</a>.
 */
public abstract class BakedBlockEntityRenderer<T extends BlockEntity, S extends BlockEntityRenderState> implements BlockEntityRenderer<T, S> {
	private static final RenderStateDataKey<Boolean> SHOULD_RENDER = RenderStateDataKey.create(() -> "shouldRender");
	private static final RenderStateDataKey<Boolean> REMOVED = RenderStateDataKey.create(() -> "removed");
	private final Map<T, S> blockEntitiesToStates = new HashMap<>();
	private final BlockEntityType<T> type;

	public BakedBlockEntityRenderer(BlockEntityRendererProvider.Context ignoredContext, BlockEntityType<T> type) {
		this.type = type;
	}

	/**
	 * Ensure that a super-call is inserted at the beginning of overridden methods.
	 *
	 * @see BlockEntityRenderer#submit(BlockEntityRenderState, PoseStack, SubmitNodeCollector, CameraRenderState)
	 */
	@Override
	public void submit(
			S state,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera
	) {
	}

	/**
	 * Ensure that a super-call is inserted at the beginning of overridden methods.
	 *
	 * @see BlockEntityRenderer#extractRenderState(BlockEntity, BlockEntityRenderState, float, Vec3, ModelFeatureRenderer.CrumblingOverlay)
	 */
	@Override
	public final void extractRenderState(
			T blockEntity,
			S state,
			float partialTicks,
			Vec3 cameraPosition,
			ModelFeatureRenderer.CrumblingOverlay breakProgress
	) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		boolean shouldBake = this.shouldBake(blockEntity);
		this.extractBakedRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

		if (shouldBake) {
			state.setData(SHOULD_RENDER, this.shouldBake(blockEntity));
			this.blockEntitiesToStates.put(blockEntity, state);
		}

		if (blockEntity.isRemoved()) {
			state.setData(REMOVED, true);
			this.blockEntitiesToStates.remove(blockEntity);
		}
	}

	/**
	 * @see BlockEntityRenderer#extractRenderState(BlockEntity, BlockEntityRenderState, float, Vec3, ModelFeatureRenderer.CrumblingOverlay)
	 */
	public void extractBakedRenderState(
			T blockEntity,
			S state,
			float partialTicks,
			Vec3 cameraPosition,
			ModelFeatureRenderer.CrumblingOverlay breakProgress
	) {
	}

	/**
	 * Render the vertices baked into the world. This is called once upon baking, and it is useful for
	 * static rendering.
	 * Use {@link #submit} for changing vertices.
	 * @return {@code true} if rendering succeeded.
	 */
	public abstract boolean bakeAndSubmit(
			S state,
			BakedRegion.CachedMultiBufferSource bufferSource
	);

	/**
	 * @return whether this {@link T} should be baked.
	 */
	public abstract boolean shouldBake(T blockEntity);

	public class Baker implements RegionBaker {
		private final BakedRegion.BakedRegionPos pos;
		private final BakedRegion.CachedMultiBufferSource cachedBufferSource;
		private final Set<S> unbakedStates = new HashSet<>();
		private final RegionBaker.RenderState renderState = new RenderState();

		public Baker(BakedRegion.BakedRegionPos pos, Identifier location) {
			this.pos = pos;
			this.cachedBufferSource = new BakedRegion.CachedMultiBufferSource(location);
		}

		@Override
		public BakedRegion.CachedMultiBufferSource getBufferSource() {
			return this.cachedBufferSource;
		}

		@Override
		public void extractState(BakedRegion.ExtractContext context) {
			this.renderState.bufferSource = this.cachedBufferSource;
			this.renderState.x = this.pos.x();
			this.renderState.y = this.pos.y();
			this.renderState.z = this.pos.z();
			ChunkAccess chunk = context.level().getChunk(this.pos.x(), this.pos.z());
			chunk.getBlockEntitiesPos().forEach(pos -> {
				Optional<T> blockEntityOptional = chunk.getBlockEntity(pos, BakedBlockEntityRenderer.this.type);
				blockEntityOptional.ifPresent(blockEntity -> {
					@SuppressWarnings("DataFlowIssue")
					S state = BakedBlockEntityRenderer.this.blockEntitiesToStates.getOrDefault(blockEntity, null);

					//noinspection ConstantValue
					if (state != null) {
						this.unbakedStates.add(state);
					}
				});
			});
		}

		@Override
		public RegionBaker.RenderState getRenderState() {
			return this.renderState;
		}

		@Override
		public void bake() {
			synchronized (BakedBlockEntityRenderer.this) {
				//noinspection ConstantValue
				assert BakedRegion.SIZE_XYZ == 16; // just in case someone changes it
				List<Runnable> removeTasks = new ArrayList<>();
				this.unbakedStates.forEach(state -> {
					if (Boolean.TRUE.equals(state.getData(REMOVED))) {
						removeTasks.add(() -> this.unbakedStates.remove(state));
					} else if (Boolean.TRUE.equals(state.getData(SHOULD_RENDER))) {
						BakedBlockEntityRenderer.this.bakeAndSubmit(
								state,
								this.cachedBufferSource.getFuture()
						);
						removeTasks.add(() -> this.unbakedStates.remove(state));
					}
				});

				removeTasks.forEach(Runnable::run);
			}
		}

		private static class RenderState extends RegionBaker.RenderState {
		}
	}
}

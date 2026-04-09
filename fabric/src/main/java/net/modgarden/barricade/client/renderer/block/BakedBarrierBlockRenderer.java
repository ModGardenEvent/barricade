package net.modgarden.barricade.client.renderer.block;

import static net.modgarden.barricade.BarricadeMod.id;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.PalettedContainer;

import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.block.StaticBarrierBlock;

import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;

public final class BakedBarrierBlockRenderer implements BarrierRegionBaker {
	public static final ChunkSectionLayer LAYER = ChunkSectionLayer.CUTOUT;
	public static final RenderPipeline RENDER_PIPELINE = RenderPipelines.CUTOUT_TERRAIN;
	private static final RandomSource RANDOM = RandomSource.createThreadLocalInstance(BarricadeMod.MEANING_OF_EVERYTHING);
	private final BakedRegion.CachedMultiBufferSource cachedBufferSource;
	private final RenderState renderState = new RenderState();

	public BakedBarrierBlockRenderer(BakedRegion.BakedRegionPos pos) {
		this.cachedBufferSource = new BakedRegion.CachedMultiBufferSource(id("static_barrier"));
		this.renderState.x = pos.x();
		this.renderState.y = pos.y();
		this.renderState.z = pos.z();
	}

	public static void reloadModels() {
		BarrierBakery2.bakeStatic(StaticBarrierBlock.BARRIERS);
	}

	@Override
	public BakedRegion.CachedMultiBufferSource getBufferSource() {
		return this.cachedBufferSource;
	}

	@Override
	public void extractState(BakedRegion.ExtractContext context) {
		BarrierRegionBaker.super.extractState(context);
		//noinspection ConstantValue
		assert BakedRegion.SIZE_XYZ == 16; // just in case someone changes i
		ChunkAccess chunk = context.level().getChunk(this.renderState.x, this.renderState.z);
		this.renderState.palettedContainer = chunk.getSection(chunk.getSectionIndexFromSectionY(this.renderState.y)).getStates();
		this.renderState.bufferSource = this.cachedBufferSource;
	}

	@Override
	public void bake() {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
		QuadEmitter emitter = Renderer.get().quadEmitter(quad ->
				quad.buffer(OverlayTexture.NO_OVERLAY, this.cachedBufferSource.getBuffer()));

		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					pos.set(x, y, z);
					BlockState state = this.renderState.palettedContainer.get(x, y, z);
					if (!(state.getBlock() instanceof StaticBarrierBlock block)) continue;
					BlockStateModel model = BarrierBakery2.getModel(block.getIdentifier());
					BlockPos offset = blockPos.setWithOffset(pos, this.renderState.x * 16, this.renderState.y * 16, this.renderState.z * 16);
					model.emitQuads(emitter, BlockAndTintGetter.EMPTY, offset, state, RANDOM, direction -> {
						if (direction == null) return false;

						BlockPos cullPos = pos.offset(direction.getUnitVec3i());
						return this.renderState.palettedContainer.get(cullPos.getX(), cullPos.getY(), cullPos.getZ()).canOcclude();
					});
				}
			}
		}
	}

	@Override
	public RegionBaker.RenderState getRenderState() {
		return this.renderState;
	}

	@SuppressWarnings("NotNullFieldNotInitialized") // always initialized by extraction
	private static class RenderState extends BarrierRegionBaker.RenderState {
		public PalettedContainer<BlockState> palettedContainer;
	}
}

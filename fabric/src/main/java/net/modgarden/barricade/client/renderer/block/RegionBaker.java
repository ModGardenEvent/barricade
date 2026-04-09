package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.level.LevelRenderState;

import net.modgarden.barricade.client.renderer.block.BakedRegion.CachedMultiBufferSource;
import net.modgarden.barricade.mixin.client.Accessor_LevelRenderer;

import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static net.modgarden.barricade.client.renderer.block.BakedBarrierBlockRenderer.RENDER_PIPELINE;

import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;

/**
 * The class responsible for baking and rendering a {@link BakedRegion}.
 */
public interface RegionBaker {
	CachedMultiBufferSource getBufferSource();

	/**
	 * Render all {@link RegionBaker} in the given {@link ReferenceSet}.
	 */
	static void render(List<BakedRegion.SectionDraw> sectionDraws) {
		Minecraft mc = Minecraft.getInstance();
		GpuDevice device = RenderSystem.getDevice();
		CommandEncoder commandEncoder = device.createCommandEncoder();
		if (mc.getMainRenderTarget().getColorTexture() == null) return;
		try (RenderPass renderPass = commandEncoder.createRenderPass(
				() -> "Barricade Blocks",
				Objects.requireNonNull(mc.getMainRenderTarget().getColorTextureView()),
				OptionalInt.empty(),
				mc.getMainRenderTarget().getDepthTextureView(),
				OptionalDouble.empty()
		)) {
			if (mc.getCameraEntity() == null) return;
			GpuTextureView atlasView = mc.getTextureManager().getTexture(QuadAtlas.BLOCK.getTextureLocation()).getTextureView();
			renderPass.bindTexture(
					"Sampler0",
					atlasView,
					((Accessor_LevelRenderer) mc.levelRenderer).barricade$getChunkLayerSampler()
			);
			renderPass.bindTexture(
					"Sampler2",
					mc.gameRenderer.lightmap(),
					RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
			);
			renderPass.setPipeline(RENDER_PIPELINE);
			RenderSystem.bindDefaultUniforms(renderPass);
			sectionDraws.forEach(sectionDraw -> {
				RenderState state = sectionDraw.state();
				// TODO: holy shit this is probably inefficient as fuck, put them in a list or something, use a shader
				//  idk. do something, don't just be lazy and "drawMultipleIndexed" on ONE thing!
				if (!state.shouldRender) return;

				renderPass.drawMultipleIndexed(List.of(sectionDraw.draw()), null, null, List.of("ChunkSection"), state.dynamicUniform);
			});
		}
	}

	/**
	 * Extract render state.
	 */
	void extractState(BakedRegion.ExtractContext context);

	/**
	 * Bake this region into its {@link CachedMultiBufferSource}.
	 */
	void bake();

	RenderState getRenderState();

	/**
	 * It's like two in the morning and I couldn't think of anything better to call a "baker factory."
	 */
	@FunctionalInterface
	interface CulinarySchool {
		RegionBaker graduate(BakedRegion.BakedRegionPos pos);
	}

	abstract class RenderState {
		public boolean shouldRender = true;
		public float opacity = 1.0f;
		public CachedMultiBufferSource bufferSource;
		public int x;
		public int y;
		public int z;
		public GpuBufferSlice dynamicUniform;
		public LevelRenderState levelRenderState;
	}
}

package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.client.renderer.block.BakedRegion.CachedMultiBufferSource;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import static net.modgarden.barricade.client.renderer.block.BakedBarrierBlockRenderer.RENDER_TYPE;

/**
 * The class responsible for baking and rendering a {@link BakedRegion}.
 */
public interface RegionBaker {
	CachedMultiBufferSource getBufferSource();

	/**
	 * Render all {@link RegionBaker} in the given {@link ReferenceSet}.
	 */
	static void render(ReferenceSet<RegionBaker> bakers, BakedRegion.RenderContext context) {
		Minecraft mc = Minecraft.getInstance();
		GpuDevice device = RenderSystem.getDevice();
		CommandEncoder commandEncoder = device.createCommandEncoder();
		if (mc.getMainRenderTarget().getColorTexture() == null) return;
		try (RenderPass renderPass = commandEncoder.createRenderPass(
				mc.getMainRenderTarget().getColorTexture(),
				OptionalInt.empty(),
				mc.getMainRenderTarget().getDepthTexture(),
				OptionalDouble.empty()
		)) {
			if (mc.getCameraEntity() == null) return;
			bakers.forEach(baker -> {
				if (!baker.shouldRender(context)) return;
				GpuBuffer indexBuffer = baker.getBufferSource().getIndexBuffer(RENDER_TYPE);
				if (indexBuffer == null) return;
				GpuBuffer vertexBuffer = baker.getBufferSource().getVertexBuffer(RENDER_TYPE);
				if (vertexBuffer == null) return;
				renderPass.setPipeline(RENDER_TYPE.getRenderPipeline());
				RENDER_TYPE.setupRenderState();
				for (int j = 0; j < 12; j++) {
					GpuTexture gpuTexture = RenderSystem.getShaderTexture(j);
					if (gpuTexture != null) {
						renderPass.bindSampler("Sampler" + j, gpuTexture);
					}
				}
				Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
				RenderSystem.setModelOffset(
						(float) -cameraPos.x(),
						(float) -cameraPos.y(),
						(float) -cameraPos.z()
				);
				renderPass.setVertexBuffer(0, vertexBuffer);
				renderPass.setIndexBuffer(indexBuffer, VertexFormat.IndexType.SHORT);
				renderPass.drawIndexed(0, indexBuffer.size());
				RENDER_TYPE.clearRenderState();
			});
		}
	}

	/**
	 * @param context the context relevant to rendering this {@link RegionBaker}.
	 * @return whether this {@link RegionBaker} should render given the context.
	 */
	default boolean shouldRender(BakedRegion.RenderContext context) {
		return true;
	}

	/**
	 * Bake this region into its {@link CachedMultiBufferSource}.
	 * @param context useful context for baking.
	 */
	void bake(BakedRegion.BakeContext context);

	/**
	 * It's like two in the morning and I couldn't think of anything better to call a "baker factory."
	 */
	@FunctionalInterface
	interface CulinarySchool {
		RegionBaker graduate(BakedRegion.BakedRegionPos pos);
	}
}

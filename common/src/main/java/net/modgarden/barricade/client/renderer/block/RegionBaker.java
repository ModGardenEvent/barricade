package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import static net.modgarden.barricade.client.renderer.block.BakedBarrierBlockRenderer.RENDER_TYPE;

/**
 * The class responsible for baking and rendering a {@link BakedRegion}.
 */
public interface RegionBaker {
	BakedRegion.CachedMultiBufferSource getBufferSource();

	default void render(BakedRegion.RenderContext context) {
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
			BakedRegion.REGIONS.forEach((pos, region) -> {
				if (BakedRegion.DIRTY_REGIONS.contains(pos)) return;
				if (mc.getCameraEntity() == null) return;
				if (!mc.getCameraEntity().position().closerThan(pos.center(), mc.levelRenderer.getLastViewDistance() * 16)) {
					BakedRegion.removeRegion(pos);
					return;
				}
				GpuBuffer indexBuffer = this.getBufferSource().getIndexBuffer(RENDER_TYPE);
				if (indexBuffer == null) return;
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
				renderPass.setVertexBuffer(0, this.getBufferSource().getVertexBuffer(RENDER_TYPE));
				renderPass.setIndexBuffer(indexBuffer, VertexFormat.IndexType.SHORT);
				renderPass.drawIndexed(0, indexBuffer.size());
				RENDER_TYPE.clearRenderState();
			});
		}
	}

	void bake(BakedRegion.UploadContext context, BakedRegion.BakedRegionPos pos);
}

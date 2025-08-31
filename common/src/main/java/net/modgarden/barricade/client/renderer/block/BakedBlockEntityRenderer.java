package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.util.WeakList;
import org.jetbrains.annotations.NotNull;

/**
 * Inspired by Glowcase's <a href="https://github.com/ModFest/glowcase/blob/b3681b46158733e632af22ea6c53afc342d9cf2e/src/main/java/dev/hephaestus/glowcase/client/render/block/entity/BakedBlockEntityRenderer.java">BakedBlockEntityRenderer</a>.
 */
public abstract class BakedBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	private final WeakList<T> blockEntities = new WeakList<>();

	public BakedBlockEntityRenderer(BlockEntityRendererProvider.Context ignoredContext) {
	}

	@Override
	public final void render(
			@NotNull T blockEntity,
			float deltaTick,
			@NotNull PoseStack poseStack,
			@NotNull MultiBufferSource bufferSource,
			int packedLight,
			int packedOverlay,
			@NotNull Vec3 cameraPos
	) {
		this.renderUnbaked(
				blockEntity,
				deltaTick,
				poseStack,
				bufferSource,
				packedLight,
				packedOverlay,
				cameraPos
		);

		if (!blockEntities.contains(blockEntity)) {
			blockEntities.add(blockEntity);
			BakedRegion.putRegion(BakedRegion.BakedRegionPos.fromBlockPos(blockEntity.getBlockPos()));
		}
	}

	/**
	 * Render the vertices immediately. This should be used for dynamically rendering.
	 * Use {@link #renderBaked} for unchanging vertices.
	 */
	public abstract void renderUnbaked(
			T blockEntity,
			float deltaTick,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			int packedOverlay,
			Vec3 cameraPos
	);

	/**
	 * Render the vertices baked into the world. This is called once upon baking, and it is useful for
	 * static rendering.
	 * Use {@link #renderUnbaked} for changing vertices.
	 * @return {@code true} if rendering succeeded.
	 */
	public abstract boolean renderBaked(
			T blockEntity,
			PoseStack poseStack,
			MultiBufferSource bufferSource
	);

	/**
	 * @return whether this {@link T} should be baked.
	 */
	public abstract boolean shouldBake(T blockEntity);

	public class Baker implements RegionBaker {
		private final BakedRegion.CachedMultiBufferSource cachedBufferSource;

		public Baker(BakedRegion.BakedRegionPos ignoredPos, ResourceLocation location) {
			this.cachedBufferSource = new BakedRegion.CachedMultiBufferSource(location);
		}

		@Override
		public BakedRegion.CachedMultiBufferSource getBufferSource() {
			return this.cachedBufferSource;
		}

		@Override
		public void bake(BakedRegion.UploadContext context) {
			BakedBlockEntityRenderer.this.blockEntities.forEach(blockEntity -> {
				if (BakedBlockEntityRenderer.this.shouldBake(blockEntity)) {
					BakedBlockEntityRenderer.this.renderBaked(
							blockEntity,
							context.poseStack(),
							this.cachedBufferSource
					);
				}
			});
		}
	}
}

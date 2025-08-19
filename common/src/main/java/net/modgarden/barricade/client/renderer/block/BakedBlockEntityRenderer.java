package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Inspired by Glowcase's <a href="https://github.com/ModFest/glowcase/blob/b3681b46158733e632af22ea6c53afc342d9cf2e/src/main/java/dev/hephaestus/glowcase/client/render/block/entity/BakedBlockEntityRenderer.java">BakedBlockEntityRenderer</a>.
 */
public abstract class BakedBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	private final BlockEntityRendererProvider.Context context;
	private boolean renderDirty;

	public BakedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.context = context;
	}

	@Override
	public final void render(
			@NotNull T blockEntity,
			float tickDelta,
			@NotNull PoseStack poseStack,
			@NotNull MultiBufferSource bufferSource,
			int packedLight,
			int packedOverlay,
			@NotNull Vec3 cameraPos
	) {
		this.renderUnbaked(
				blockEntity,
				tickDelta,
				poseStack,
				bufferSource,
				packedLight,
				packedOverlay,
				cameraPos
		);
	}

	/**
	 * Render the vertices immediately. This should be used for dynamically rendering.
	 * Use {@link #renderBaked} for unchanging vertices.
	 */
	public abstract void renderUnbaked(
			T blockEntity,
			float tickDelta,
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
	 */
	public abstract void renderBaked(
			T blockEntity,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			int packedOverlay
	);

	/**
	 * @return whether this {@link T} should be baked.
	 */
	public abstract boolean shouldBake(T blockEntity);

	protected void markForRebuild() {
		this.renderDirty = true;
	}

	protected BlockEntityRendererProvider.Context getContext() {
		return this.context;
	}
}

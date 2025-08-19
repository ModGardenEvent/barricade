package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;

public class AdvancedBarrierBlockRenderer extends BakedBlockEntityRenderer<AdvancedBarrierBlockEntity> {
	public AdvancedBarrierBlockRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderUnbaked(
			AdvancedBarrierBlockEntity blockEntity,
			float tickDelta,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			int packedOverlay,
			Vec3 cameraPos
	) {
	}

	@Override
	public void renderBaked(
			AdvancedBarrierBlockEntity blockEntity,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			int packedOverlay
	) {
	}

	@Override
	public boolean shouldBake(AdvancedBarrierBlockEntity blockEntity) {
		return true;
	}
}

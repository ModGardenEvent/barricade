package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;

public class AdvancedBarrierBlockRenderer implements BlockEntityRenderer<AdvancedBarrierBlockEntity> {
	@Override
	public void render(
			AdvancedBarrierBlockEntity blockEntity,
			float partialTick,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			int packedOverlay,
			Vec3 cameraPos
	) {
	}
}

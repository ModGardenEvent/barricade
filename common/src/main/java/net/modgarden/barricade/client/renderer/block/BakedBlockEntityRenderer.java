package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class BakedBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	@Override
	public final void render(
			@NotNull T blockEntity,
			float partialTick,
			@NotNull PoseStack poseStack,
			@NotNull MultiBufferSource bufferSource,
			int packedLight,
			int packedOverlay,
			@NotNull Vec3 cameraPos
	) {
	}
}

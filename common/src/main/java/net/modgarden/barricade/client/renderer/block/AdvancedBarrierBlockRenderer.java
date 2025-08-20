package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.registry.BarricadeRegistries;

import java.util.HashMap;
import java.util.Map;

public class AdvancedBarrierBlockRenderer extends BakedBlockEntityRenderer<AdvancedBarrierBlockEntity> {
	private static final Map<ResourceLocation, BlockStateModel> MODELS = new HashMap<>();

	public AdvancedBarrierBlockRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void renderUnbaked(
			AdvancedBarrierBlockEntity blockEntity,
			float deltaTick,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			int packedOverlay,
			Vec3 cameraPos
	) {
	}

	@Override
	public boolean renderBaked(
			AdvancedBarrierBlockEntity blockEntity,
			PoseStack poseStack,
			MultiBufferSource bufferSource
	) {
		if (blockEntity.getLevel() == null) return false;
		Registry<AdvancedBarrier> advancedBarrierRegistry = blockEntity.getLevel().registryAccess().lookupOrThrow(BarricadeRegistries.ADVANCED_BARRIER);
		if (MODELS.isEmpty() && !advancedBarrierRegistry.keySet().isEmpty()) {
			BarrierBakery.bakeModels(advancedBarrierRegistry, MODELS);
		} else if (MODELS.isEmpty()) return false;
		AdvancedBarrier data = blockEntity.getData();
		ResourceLocation barrierId = advancedBarrierRegistry.getKey(data);
		BlockStateModel model = MODELS.get(barrierId);
		if (model == null) return false;
		Minecraft mc = Minecraft.getInstance();
		mc.getBlockRenderer().renderBatched(
				blockEntity.getBlockState(),
				blockEntity.getBlockPos(),
				blockEntity.getLevel(),
				poseStack,
				bufferSource.getBuffer(BakedBarrierBlockRenderer.RENDER_TYPE),
				true,
				model.collectParts(blockEntity.getLevel().getRandom())
		);

		return true;
	}

	@Override
	public boolean shouldBake(AdvancedBarrierBlockEntity blockEntity) {
		return true;
	}

	public static void reloadModels() {
		MODELS.clear();
	}
}

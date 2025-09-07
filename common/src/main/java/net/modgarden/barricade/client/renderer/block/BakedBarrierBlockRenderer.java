package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.StaticBarrierBlock;
import net.modgarden.barricade.registry.BarricadeBlocks;

import java.util.HashMap;
import java.util.Map;

public final class BakedBarrierBlockRenderer implements BarrierRegionBaker {
	public static final Map<ResourceLocation, BlockStateModel> MODELS = new HashMap<>();
	public static final RenderType RENDER_TYPE = RenderType.cutout();
	private final BakedRegion.CachedMultiBufferSource cachedBufferSource;
	private final BakedRegion.BakedRegionPos pos;

	public BakedBarrierBlockRenderer(BakedRegion.BakedRegionPos pos) {
		this.pos = pos;
		this.cachedBufferSource = new BakedRegion.CachedMultiBufferSource(Barricade.asResource("static_barrier"));
	}

	public static void reloadModels() {
		MODELS.clear();
		BarrierBakery.bakeModels(StaticBarrierBlock.BARRIERS, MODELS);
		BarrierBakery.bakeModels(ResourceLocation.withDefaultNamespace("barrier"), Barricade.asResource("block/barrier"), MODELS);
	}

	@Override
	public BakedRegion.CachedMultiBufferSource getBufferSource() {
		return this.cachedBufferSource;
	}

	@Override
	public void bake(BakedRegion.BakeContext context) {
		//noinspection ConstantValue
		assert BakedRegion.SIZE_XYZ == 16; // just in case someone changes it
		ChunkAccess chunk = context.level().getChunk(this.pos.x(), this.pos.z());
		PalettedContainer<BlockState> states = chunk.getSection(chunk.getSectionIndexFromSectionY(this.pos.y())).getStates();
		Minecraft mc = Minecraft.getInstance();
		VertexConsumer vertexConsumer = cachedBufferSource.getBuffer(RENDER_TYPE);
		BlockPos regionPos = this.pos.lowerCorner();
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					BlockState state = states.get(x, y, z);
					if (!Barricade.isOperatorModel(state) || state.is(BarricadeBlocks.ADVANCED_BARRIER.get())) continue;
					blockPos.set(x + regionPos.getX(), y + regionPos.getY(), z + regionPos.getZ());
					PoseStack poseStack = new PoseStack();
					poseStack.pushPose();
					poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
					mc.getBlockRenderer().renderBatched(
							state,
							blockPos,
							context.level(),
							poseStack,
							vertexConsumer,
							true,
							MODELS.get(BuiltInRegistries.BLOCK.getKey(state.getBlock())).collectParts(context.level().getRandom())
					);
					poseStack.popPose();
				}
			}
		}
	}
}

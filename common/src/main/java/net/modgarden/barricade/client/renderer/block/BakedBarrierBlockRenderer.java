package net.modgarden.barricade.client.renderer.block;

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

import java.lang.ref.Cleaner;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public final class BakedBarrierBlockRenderer implements RegionBaker {
	public static final Map<ResourceLocation, BlockStateModel> MODELS = new HashMap<>();
	public static final RenderType RENDER_TYPE = RenderType.cutout();
	private static final Cleaner CLEANER = Cleaner.create();
	private final BakedRegion.CachedMultiBufferSource cachedBufferSource = new BakedRegion.CachedMultiBufferSource();
	public static final BakedBarrierBlockRenderer INSTANCE = new BakedBarrierBlockRenderer();

	private BakedBarrierBlockRenderer() {
		WeakReference<RegionBaker> baker = new WeakReference<>(this);
		BakedRegion.registerRegionBaker(baker);
		CLEANER.register(this, new CleanerState(baker));
	}

	public static void reloadModels() {
		MODELS.clear();
		BarrierBakery.bakeModels(StaticBarrierBlock.BARRIERS, MODELS);
	}

	@Override
	public BakedRegion.CachedMultiBufferSource getBufferSource() {
		return this.cachedBufferSource;
	}

	@Override
	public void bake(BakedRegion.UploadContext context, BakedRegion.BakedRegionPos pos) {
		ChunkAccess chunk = context.level().getChunk(pos.x(), pos.z());
		PalettedContainer<BlockState> states = chunk.getSection(chunk.getSectionIndexFromSectionY(pos.y())).getStates();
		Minecraft mc = Minecraft.getInstance();
		VertexConsumer vertexConsumer = cachedBufferSource.getBuffer(RENDER_TYPE);
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					BlockState state = states.get(x, y, z);
					if (!Barricade.isOperatorModel(state) || state.is(BarricadeBlocks.ADVANCED_BARRIER.get())) continue;
					mc.getBlockRenderer().renderBatched(
							state,
							new BlockPos(x, y, z),
							context.level(),
							context.poseStack(),
							vertexConsumer,
							true,
							MODELS.get(BuiltInRegistries.BLOCK.getKey(state.getBlock())).collectParts(context.level().getRandom())
					);
				}
			}
		}
	}
}

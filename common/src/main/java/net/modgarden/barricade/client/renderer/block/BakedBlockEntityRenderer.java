package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Inspired by Glowcase's <a href="https://github.com/ModFest/glowcase/blob/b3681b46158733e632af22ea6c53afc342d9cf2e/src/main/java/dev/hephaestus/glowcase/client/render/block/entity/BakedBlockEntityRenderer.java">BakedBlockEntityRenderer</a>.
 */
public abstract class BakedBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	private final List<T> unbakedBlockEntities = new ArrayList<>();
	private final BlockEntityType<T> type;

	public BakedBlockEntityRenderer(BlockEntityRendererProvider.Context ignoredContext, BlockEntityType<T> type) {
		this.type = type;
	}

	@Override
	public synchronized final void render(
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

//		synchronized (BakedRegion.DIRTY_REGIONS) {
//			if (BakedRegion.DIRTY_REGIONS.contains(BakedRegion.BakedRegionPos.fromBlockPos(blockEntity.getBlockPos())) && !unbakedBlockEntities.contains(blockEntity)) {
//				unbakedBlockEntities.add(blockEntity);
//			}
//		}
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
		private final BakedRegion.BakedRegionPos pos;
		private final BakedRegion.CachedMultiBufferSource cachedBufferSource;

		public Baker(BakedRegion.BakedRegionPos pos, ResourceLocation location) {
			this.pos = pos;
			this.cachedBufferSource = new BakedRegion.CachedMultiBufferSource(location);
		}

		@Override
		public BakedRegion.CachedMultiBufferSource getBufferSource() {
			return this.cachedBufferSource;
		}

		@Override
		public void bake(BakedRegion.BakeContext context) {
			synchronized (BakedBlockEntityRenderer.this) {
				ChunkAccess chunk = context.level().getChunk(this.pos.x(), this.pos.z());
				chunk.getBlockEntitiesPos().forEach(pos -> {
					Optional<T> blockEntity = chunk.getBlockEntity(pos, BakedBlockEntityRenderer.this.type);
					blockEntity.ifPresent(BakedBlockEntityRenderer.this.unbakedBlockEntities::add);
				});
				PoseStack poseStack = new PoseStack();
				List<Runnable> removeTasks = new ArrayList<>();
				BakedBlockEntityRenderer.this.unbakedBlockEntities.forEach(blockEntity -> {
					if (!this.pos.contains(blockEntity.getBlockPos())) return;
					if (blockEntity.isRemoved()) removeTasks.add(() -> BakedBlockEntityRenderer.this.unbakedBlockEntities.remove(blockEntity));
					if (BakedBlockEntityRenderer.this.shouldBake(blockEntity)) {
						BakedBlockEntityRenderer.this.renderBaked(
								blockEntity,
								poseStack,
								this.cachedBufferSource
						);
						removeTasks.add(() -> BakedBlockEntityRenderer.this.unbakedBlockEntities.remove(blockEntity));
					}
				});

				removeTasks.forEach(Runnable::run);
			}
		}
	}
}

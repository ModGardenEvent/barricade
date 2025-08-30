package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ReferenceArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.util.WeakList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * A region in which block entity vertices are baked.
 */
public class BakedRegion {
	/**
	 * The size of a {@link BakedRegion}. This is always a power of 2.
	 */
	public static final int SIZE_XYZ = 16;
	static final Object2ReferenceMap<BakedRegionPos, BakedRegion> REGIONS = new Object2ReferenceArrayMap<>();
	static final Set<BakedRegionPos> DIRTY_REGIONS = new HashSet<>();
	private static final WeakList<RegionBaker> REGION_BAKERS = new WeakList<>();

	private final BakedRegionPos pos;
	private final WeakList<RegionBaker> regionBakers;

	public BakedRegion(BakedRegionPos pos, WeakList<RegionBaker> regionBakers) {
		this.pos = pos;
		this.regionBakers = regionBakers;
	}

	/**
	 * Render all regions.
	 */
	public static void renderRegions() {
		REGIONS.values().forEach(BakedRegion::render);
	}

	/**
	 * Build all dirty regions.
	 */
	public static void buildDirty(UploadContext context) {
		for (BakedRegionPos pos : DIRTY_REGIONS) {
			BakedRegion region = REGIONS.get(pos);
			region.upload(context);
		}
		DIRTY_REGIONS.clear();
	}

	/**
	 * Register a region if it does not already exist.
	 */
	static void putRegion(BakedRegionPos pos) {
		REGIONS.putIfAbsent(pos, new BakedRegion(pos, REGION_BAKERS));
		markRegionDirty(pos);
	}

	/**
	 * Mark the {@link BakedRegion} to be rebuilt.
	 */
	public static void markRegionDirty(BakedRegionPos pos) {
		DIRTY_REGIONS.add(pos);
	}

	/**
	 * Register a new {@link RegionBaker} for baking.
	 */
	public static void registerRegionBaker(WeakReference<RegionBaker> baker) {
		REGION_BAKERS.addWeak(baker);
	}

	/**
	 * Remove a {@link RegionBaker}.
	 */
	public static void removeRegionBaker(WeakReference<RegionBaker> baker) {
		REGION_BAKERS.removeWeak(baker);
	}

	/**
	 * Render the built buffers.
	 */
	public void render() {
		if (DIRTY_REGIONS.contains(this.pos)) return;
		try {
			this.regionBakers.forEach(RegionBaker::render);
		} catch(Exception e) {
			Barricade.LOG.error("Exception during BakedRegion rendering", e);
		}
	}

	/**
	 * Upload this region to the GPU.
	 */
	public void upload(UploadContext context) {
		try {
			this.regionBakers.forEach(baker -> {
				baker.bake(context, this.pos);
				baker.getBufferSource().upload();
			});
		} catch(Exception e) {
			Barricade.LOG.error("Exception during BakedRegion uploading", e);
		}
	}

	public record UploadContext(LevelAccessor level, PoseStack poseStack) {}

	public static class CachedMultiBufferSource implements MultiBufferSource, AutoCloseable {
		private final Map<RenderType, BufferBuilder> buffers = new HashMap<>();
		private final Map<RenderType, ByteBufferBuilder> byteBuffers = new HashMap<>();
		private final Map<RenderType, MeshData> meshes = new HashMap<>();
		private final Map<RenderType, GpuBuffer> vertexBuffers = new HashMap<>();
		private final Map<RenderType, GpuBuffer> indexBuffers = new HashMap<>();

		@Override
		public @NotNull VertexConsumer getBuffer(@NotNull RenderType renderType) {
			return this.buffers.computeIfAbsent(
					renderType,
					key -> new BufferBuilder(
							this.byteBuffers.computeIfAbsent(
									renderType,
									key1 -> new ByteBufferBuilder(renderType.bufferSize())
							),
							renderType.mode(),
							renderType.format()
					)
			);
		}

		public void upload() {
			this.buffers.forEach((renderType, bufferBuilder) -> {
				MeshData meshData = this.meshes.put(renderType, bufferBuilder.build());
				assert meshData != null;
				this.vertexBuffers.put(
						renderType,
						renderType.format().uploadImmediateVertexBuffer(meshData.vertexBuffer())
				);
				this.indexBuffers.put(
						renderType,
						renderType.format().uploadImmediateIndexBuffer(Objects.requireNonNull(
								meshData.indexBuffer(),
								"Baked Region index buffer failed to build"
						))
				);
			});
		}

		public GpuBuffer getVertexBuffer(RenderType renderType) {
			return this.vertexBuffers.get(renderType);
		}

		public @Nullable GpuBuffer getIndexBuffer(RenderType renderType) {
			return this.indexBuffers.get(renderType);
		}

		public void flush() {
			this.byteBuffers.forEach((key, buffer) -> buffer.clear());
			this.buffers.clear();
			this.meshes.forEach((key, meshData) -> meshData.close());
			this.meshes.clear();
			this.vertexBuffers.forEach((key, buffer) -> buffer.close());
			this.vertexBuffers.clear();
			this.indexBuffers.forEach((key, buffer) -> buffer.close());
			this.indexBuffers.clear();
		}

		@Override
		public void close() {
			this.flush();
			this.byteBuffers.forEach((key, buffer) -> buffer.close());
			this.byteBuffers.clear();
		}
	}

	public record BakedRegionPos(int x, int y, int z) {
		public static BakedRegionPos fromBlockPos(BlockPos pos) {
			return new BakedRegionPos(pos.getX() / SIZE_XYZ, pos.getY() / SIZE_XYZ, pos.getZ() / SIZE_XYZ);
		}

		public Vec3 center() {
			float middle = SIZE_XYZ / 2.0f;
			return new Vec3(
					(this.x() * SIZE_XYZ) + middle,
					(this.y() * SIZE_XYZ) + middle,
					(this.z() * SIZE_XYZ) + middle
			);
		}
	}
}

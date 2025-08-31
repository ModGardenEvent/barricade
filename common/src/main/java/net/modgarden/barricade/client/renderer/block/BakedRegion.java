package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ReferenceArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.renderer.block.RegionBaker.CulinarySchool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

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
	private static final Set<CulinarySchool> CULINARY_SCHOOLS = new HashSet<>();
	private static final List<Runnable> REGION_REMOVE_TASKS = new ArrayList<>();

	private final BakedRegionPos pos;
	private final List<RegionBaker> regionBakers;

	public BakedRegion(BakedRegionPos pos, List<RegionBaker> regionBakers) {
		this.pos = pos;
		this.regionBakers = regionBakers;
	}

	/**
	 * Render all regions.
	 */
	public static void renderRegions(RenderContext context) {
		REGIONS.values().forEach(region -> region.render(context));
		REGION_REMOVE_TASKS.forEach(Runnable::run);
	}

	/**
	 * Build all dirty regions.
	 */
	public static void buildDirty(UploadContext context) {
		for (BakedRegionPos pos : DIRTY_REGIONS) {
			BakedRegion region = REGIONS.get(pos);
			if (region == null) continue;
			region.upload(context);
		}
		DIRTY_REGIONS.clear();
	}

	/**
	 * Queue a region removal.
	 */
	static void removeRegion(BakedRegionPos pos) {
		REGION_REMOVE_TASKS.add(() -> REGIONS.remove(pos));
	}

	/**
	 * Register a region if it does not already exist.
	 */
	public static void putRegion(BakedRegionPos pos) {
		REGIONS.putIfAbsent(pos, new BakedRegion(pos, graduateBakers(pos)));
		markRegionDirty(pos);
	}

	/**
	 * Mark the {@link BakedRegion} to be rebuilt.
	 */
	public static void markRegionDirty(BakedRegionPos pos) {
		DIRTY_REGIONS.add(pos);
	}

	/**
	 * Register a new {@link CulinarySchool} for baking.
	 */
	public static void registerRegionBaker(CulinarySchool baker) {
		CULINARY_SCHOOLS.add(baker);
	}

	private static List<RegionBaker> graduateBakers(BakedRegionPos pos) {
		return CULINARY_SCHOOLS.stream()
				.map(baker -> baker.graduate(pos))
				.collect(Collectors.toList());
	}

	/**
	 * Render the built buffers.
	 */
	public void render(RenderContext context) {
		if (DIRTY_REGIONS.contains(this.pos)) return;
		try {
			this.regionBakers.forEach(baker -> baker.render(context));
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
				if (baker.getBufferSource().isUploaded()) {
					baker.getBufferSource().flush();
				}
				baker.bake(context);
				baker.getBufferSource().upload();
			});
		} catch(Exception e) {
			Barricade.LOG.error("Exception during BakedRegion uploading", e);
		}
	}

	public record UploadContext(LevelAccessor level, PoseStack poseStack) {}

	public record RenderContext() {}

	public static class CachedMultiBufferSource implements MultiBufferSource, AutoCloseable {
		private final Map<RenderType, BufferBuilder> buffers = new HashMap<>();
		private final Map<RenderType, ByteBufferBuilder> byteBuffers = new HashMap<>();
		private final Map<RenderType, MeshData> meshes = new HashMap<>();
		private final Map<RenderType, GpuBuffer> vertexBuffers = new HashMap<>();
		private final Map<RenderType, GpuBuffer> indexBuffers = new HashMap<>();
		private boolean isUploaded;

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

		public boolean isUploaded() {
			return this.isUploaded;
		}

		public void upload() {
			if (this.isUploaded) return;
			this.isUploaded = true;

			this.buffers.forEach((renderType, bufferBuilder) -> {
				MeshData meshData = bufferBuilder.build();
				if (meshData == null) {
					return;
				}
				meshData.sortQuads(this.byteBuffers.get(renderType), VertexSorting.ORTHOGRAPHIC_Z);
				this.meshes.put(renderType, meshData);
				GpuDevice gpu = RenderSystem.getDevice();
				this.vertexBuffers.put(
						renderType,
						gpu.createBuffer(
								() -> Barricade.MOD_NAME + " BakedRegion Vertex Buffer",
								BufferType.VERTICES,
								BufferUsage.DYNAMIC_WRITE,
								meshData.vertexBuffer()
						)
				);
				this.indexBuffers.put(
						renderType,
						gpu.createBuffer(
								() -> Barricade.MOD_NAME + " BakedRegion Index Buffer",
								BufferType.VERTICES,
								BufferUsage.DYNAMIC_WRITE,
								Objects.requireNonNull(
										meshData.indexBuffer(),
										"Baked Region index buffer failed to build"
								)
						)
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
			this.isUploaded = false;
			this.byteBuffers.forEach((key, buffer) -> buffer.discard());
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

		public BlockPos lowerCorner() {
			return new BlockPos(
					this.x() * SIZE_XYZ,
					this.y() * SIZE_XYZ,
					this.z() * SIZE_XYZ
			);
		}
	}
}

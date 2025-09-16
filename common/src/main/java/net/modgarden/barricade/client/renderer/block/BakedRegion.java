package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.renderer.block.RegionBaker.CulinarySchool;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * A region in which block entity vertices are baked.
 */
public class BakedRegion {
	/**
	 * The size of a {@link BakedRegion}. This is always a power of 2.
	 */
	public static final int SIZE_XYZ = 16;
	/**
	 * How much you have to shift right to get to {@link #SIZE_XYZ}.
	 */
	public static final int SHIFT_XYZ = 4;
	static final Object2ReferenceMap<BakedRegionPos, BakedRegion> REGIONS = new Object2ReferenceOpenHashMap<>();
	static final ReferenceSet<RegionBaker> BAKERS_TO_RENDER = new ReferenceOpenHashSet<>();
	static final Set<BakedRegionPos> DIRTY_REGIONS = new HashSet<>();
	/**
	 * A set of transient regions that are baked but not yet uploaded.
	 */
	private static final Set<BakedRegionPos> BAKED_REGIONS = new HashSet<>();
	private static final Set<CulinarySchool> CULINARY_SCHOOLS = new HashSet<>();
	private static final List<Runnable> REGION_REMOVE_TASKS = new ArrayList<>();

	private final List<RegionBaker> regionBakers;

	public BakedRegion(List<RegionBaker> regionBakers) {
		this.regionBakers = regionBakers;
	}

	/**
	 * Render all regions.
	 */
	public static void renderRegions(RenderContext context) {
		try {
			RegionBaker.render(BAKERS_TO_RENDER, context);
		} catch(Exception e) {
			Barricade.LOG.error("Exception during BakedRegion rendering", e);
		}
	}

	/**
	 * Bake all dirty regions.<br>
	 * This can be called at any point in time before rendering occurs and can
	 * finish at any point afterward, even into the next frame.<br>
	 * <br>
	 * This method bakes regions <b>asynchronously</b>, but it may block the
	 * thread when {@link #DIRTY_REGIONS} is locked.
	 */
	public static void bakeDirty(BakeContext context) {
		synchronized (DIRTY_REGIONS) {
			for (BakedRegionPos pos : DIRTY_REGIONS) {
				CompletableFuture.supplyAsync(() -> {
					BakedRegion region = REGIONS.get(pos);
					if (region == null) return null;
					region.bake(context);
					synchronized (BAKERS_TO_RENDER) {
						BAKERS_TO_RENDER.addAll(region.regionBakers);
					}
					return pos;
				}).thenAcceptAsync(pos1 -> {
					if (pos1 == null) return;
					synchronized (DIRTY_REGIONS) {
						DIRTY_REGIONS.remove(pos1);
					}
					synchronized (BAKED_REGIONS) {
						BAKED_REGIONS.add(pos1);
					}
				}).exceptionally(t -> {
					// deal with faulty/failed region bake tasks
					if (t instanceof TimeoutException) {
						Barricade.LOG.error("Region baking at {} took too long!", pos);
					} else {
						Barricade.LOG.error("Region baking at {}", pos);
					}
					Barricade.LOG.error("Region baking failed", t);
					return null;
				}).orTimeout(1, TimeUnit.SECONDS);
			}
		}
	}

	/**
	 * Upload all dirty regions.<br>
	 * This is typically called at the end of a frame but may be called
	 * at any point during rendering.
	 */
	public static void uploadDirty() {
		List<Runnable> removeTasks = new ArrayList<>();
		synchronized (BAKED_REGIONS) {
			for (BakedRegionPos pos : BAKED_REGIONS) {
				BakedRegion region = REGIONS.get(pos);
				if (region == null) continue;
				region.upload();
				removeTasks.add(() -> BAKED_REGIONS.remove(pos));
			}
			removeTasks.forEach(Runnable::run);
		}
	}

	/**
	 * Called when the current frame has ended rendering.
	 * We clean some things up here.
	 */
	public static void onRenderEnd() {
		REGION_REMOVE_TASKS.forEach(Runnable::run);
		REGION_REMOVE_TASKS.clear();
	}

	/**
	 * Queue a region removal.
	 */
	public static void removeRegion(BakedRegionPos pos) {
		REGION_REMOVE_TASKS.add(() -> {
			BakedRegion region = REGIONS.get(pos);
			if (region == null) return;
			synchronized (BAKERS_TO_RENDER) {
				region.regionBakers.forEach(regionBaker -> {
					regionBaker.getBufferSource().close();
					BAKERS_TO_RENDER.remove(regionBaker);
				});
			}
			DIRTY_REGIONS.remove(pos);
			REGIONS.remove(pos);
		});
	}

	public static void clearRegions() {
		REGIONS.forEach((pos, region) -> removeRegion(pos));
		REGION_REMOVE_TASKS.forEach(Runnable::run);
		REGION_REMOVE_TASKS.clear();
	}

	/**
	 * Register a region if it does not already exist and mark it dirty.
	 */
	public static void putRegion(BakedRegionPos pos) {
		REGIONS.putIfAbsent(pos, new BakedRegion(graduateBakers(pos)));
		markRegionDirty(pos);
	}

	/**
	 * Register a region and its neighbors if they do not already exist and mark them dirty.
	 */
	public static void putRegionAndNeighbors(BakedRegionPos pos) {
		for (int x = pos.x() - 1; x <= pos.x() + 1; x++) {
			for (int y = pos.y() - 1; y <= pos.y() + 1; y++) {
				for (int z = pos.z() - 1; z <= pos.z() + 1; z++) {
					putRegion(new BakedRegion.BakedRegionPos(x, y, z));
				}
			}
		}
	}

	/**
	 * Mark the {@link BakedRegion} to be rebuilt.
	 */
	public static void markRegionDirty(BakedRegionPos pos) {
		synchronized (DIRTY_REGIONS) {
			DIRTY_REGIONS.add(pos);
		}
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
	 * Bake this region.
	 * @param context relevant context for baking.
	 */
	public void bake(BakeContext context) {
		try {
			synchronized (this.regionBakers) {
				this.regionBakers.forEach(baker -> baker.bake(context));
			}
		} catch(Exception e) {
			Barricade.LOG.error("Exception during BakedRegion baking", e);
		}
	}

	/**
	 * Upload this region to the GPU.
	 */
	public void upload() {
		try {
			this.regionBakers.forEach(baker -> baker.getBufferSource().upload());
		} catch(Exception e) {
			Barricade.LOG.error("Exception during BakedRegion uploading", e);
		}

	}

	public record BakeContext(LevelAccessor level) {}

	public record RenderContext(Player player, float deltaTick) {}

	public static class CachedMultiBufferSource implements MultiBufferSource, AutoCloseable {
		private final @Nullable CachedMultiBufferSource future;
		private final Map<RenderType, BufferBuilder> buffers = new HashMap<>();
		private final Map<RenderType, ByteBufferBuilder> byteBuffers = new HashMap<>();
		private final Map<RenderType, MeshData> meshes = new HashMap<>();
		private final Map<RenderType, GpuBuffer> vertexBuffers = new HashMap<>();
		private final Map<RenderType, GpuBuffer> indexBuffers = new HashMap<>();
		private final ResourceLocation bakerLocation;

		public CachedMultiBufferSource(ResourceLocation bakerLocation) {
			this(bakerLocation, ofFuture(bakerLocation));
		}

		private CachedMultiBufferSource(ResourceLocation bakerLocation, @Nullable CachedMultiBufferSource future) {
			this.bakerLocation = bakerLocation.withPrefix("baker/");
			this.future = future;
		}

		private static CachedMultiBufferSource ofFuture(ResourceLocation bakerLocation) {
			return new CachedMultiBufferSource(bakerLocation, null);
		}

		/**
		 * The future CMBS that is being rendered to.
		 * @return the new CMBS that is being rendered to.
		 */
		public CachedMultiBufferSource getFuture() {
			this.assertNotFuture(this.future);
			return this.future;
		}

		/**
		 * Move all data from the future CMBS to this CMBS used for rendering.
		 */
		private void update() {
			this.assertNotFuture(this.future);
			this.closeSelf();
			this.buffers.putAll(this.future.buffers);
			this.byteBuffers.putAll(this.future.byteBuffers);
			this.meshes.putAll(this.future.meshes);
			this.vertexBuffers.putAll(this.future.vertexBuffers);
			this.indexBuffers.putAll(this.future.indexBuffers);
			// clear the future CMBS
			this.future.buffers.clear();
			this.future.byteBuffers.clear();
			this.future.meshes.clear();
			this.future.vertexBuffers.clear();
			this.future.indexBuffers.clear();
		}

		@Contract("null -> fail")
		private void assertNotFuture(CachedMultiBufferSource old) {
			if (old == null) {
				throw new IllegalStateException("Cannot get the old CachedMultiBufferSource of an old CachedMultiBufferSource");
			}
		}

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
			this.assertNotFuture(this.future);

			this.future.buffers.forEach((renderType, bufferBuilder) -> {
				MeshData meshData = bufferBuilder.build();
				if (meshData == null) {
					return;
				}
				meshData.sortQuads(this.future.byteBuffers.get(renderType), VertexSorting.ORTHOGRAPHIC_Z);
				this.future.meshes.put(renderType, meshData);
				GpuDevice gpu = RenderSystem.getDevice();
				this.future.vertexBuffers.put(
						renderType,
						gpu.createBuffer(
								() -> this.bakerLocation.toString() + " Vertex Buffer",
								BufferType.VERTICES,
								BufferUsage.DYNAMIC_WRITE,
								meshData.vertexBuffer()
						)
				);
				this.future.indexBuffers.put(
						renderType,
						gpu.createBuffer(
								() -> this.bakerLocation.toString() + " Index Buffer",
								BufferType.INDICES,
								BufferUsage.DYNAMIC_WRITE,
								Objects.requireNonNull(
										meshData.indexBuffer(),
										"Baked Region index buffer failed to build"
								)
						)
				);
			});

			// Move everything to this CMBS
			this.update();
		}

		public GpuBuffer getVertexBuffer(RenderType renderType) {
			return this.vertexBuffers.get(renderType);
		}

		public @Nullable GpuBuffer getIndexBuffer(RenderType renderType) {
			return this.indexBuffers.get(renderType);
		}

		public void flush() {
			this.byteBuffers.forEach((key, buffer) -> buffer.discard());
			this.buffers.clear();
			this.meshes.forEach((key, meshData) -> meshData.close());
			this.meshes.clear();
			this.vertexBuffers.forEach((key, buffer) -> buffer.close());
			this.vertexBuffers.clear();
			this.indexBuffers.forEach((key, buffer) -> buffer.close());
			this.indexBuffers.clear();
		}

		private void closeSelf() {
			this.flush();
			this.byteBuffers.forEach((key, buffer) -> buffer.close());
			this.byteBuffers.clear();
		}

		@Override
		public void close() {
			this.closeSelf();
			if (this.future != null) this.future.close();
		}

	}

	public record BakedRegionPos(int x, int y, int z) {
		public static BakedRegionPos fromBlockPos(BlockPos pos) {
			return new BakedRegionPos(pos.getX() >> SHIFT_XYZ, pos.getY() >> SHIFT_XYZ, pos.getZ() >> SHIFT_XYZ);
		}

		public boolean contains(BlockPos pos) {
			BakedRegionPos regionPos = fromBlockPos(pos);
			return regionPos.equals(this);
		}

		public Vec3 center() {
			float middle = SIZE_XYZ / 2.0f;
			return new Vec3(
					(this.x() << SHIFT_XYZ) + middle,
					(this.y() << SHIFT_XYZ) + middle,
					(this.z() << SHIFT_XYZ) + middle
			);
		}

		public BlockPos lowerCorner() {
			return new BlockPos(
					this.x() << SHIFT_XYZ,
					this.y() << SHIFT_XYZ,
					this.z() << SHIFT_XYZ
			);
		}
	}
}

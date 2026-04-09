package net.modgarden.barricade.client.renderer.block;

import static net.modgarden.barricade.client.renderer.block.BakedBarrierBlockRenderer.RENDER_PIPELINE;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.client.renderer.block.RegionBaker.CulinarySchool;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;

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
	static final ReferenceSet<RegionBaker.RenderState> STATES_TO_RENDER = new ReferenceOpenHashSet<>();
	static final List<SectionDraw> SECTION_DRAWS = new ArrayList<>();
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
	public static void renderRegions() {
		try {
			compileDraws();
			RegionBaker.render(SECTION_DRAWS);
		} catch(Exception e) {
			BarricadeMod.LOG.error("Exception during BakedRegion rendering", e);
		}
	}

	public static void extract(ExtractContext context) {
		synchronized (DIRTY_REGIONS) {
			for (Map.Entry<BakedRegionPos, BakedRegion> entry : REGIONS.entrySet()) {
				BakedRegionPos pos = entry.getKey();
				BakedRegion region = entry.getValue();
				CompletableFuture.supplyAsync(() -> {
					region.regionBakers.forEach(baker -> baker.extractState(context));
					return region;
				}).exceptionally(t -> {
					// deal with faulty/failed region extraction tasks
					if (t instanceof TimeoutException) {
						BarricadeMod.LOG.error("Region extraction at {} took too long!", pos);
					} else {
						BarricadeMod.LOG.error("Region extraction at {}", pos);
					}
					BarricadeMod.LOG.error("Region extraction failed", t);
					return null;
				}).orTimeout(1, TimeUnit.SECONDS);
			}
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
	public static void bakeDirty() {
		synchronized (DIRTY_REGIONS) {
			for (BakedRegionPos pos : DIRTY_REGIONS) {
				CompletableFuture.supplyAsync(() -> {
					BakedRegion region = REGIONS.get(pos);
					//noinspection ConstantValue
					if (region == null) return null;
					region.bake();
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
						BarricadeMod.LOG.error("Region baking at {} took too long!", pos);
					} else {
						BarricadeMod.LOG.error("Region baking at {}", pos);
					}
					BarricadeMod.LOG.error("Region baking failed", t);
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
				//noinspection ConstantValue
				if (region == null) continue;
				region.upload();
				removeTasks.add(() -> BAKED_REGIONS.remove(pos));
				for (RegionBaker baker : region.regionBakers) {
					STATES_TO_RENDER.add(baker.getRenderState());
				}
			}
			removeTasks.forEach(Runnable::run);
			compileDraws();
		}
	}

	private static void compileDraws() {
		// TODO: holy shit this is probably inefficient as fuck
		SECTION_DRAWS.clear();
		Minecraft mc = Minecraft.getInstance();
		GpuTextureView atlasView = mc.getTextureManager().getTexture(QuadAtlas.BLOCK.getTextureLocation()).getTextureView();
		STATES_TO_RENDER.forEach(state -> {
			GpuBuffer indexBuffer = state.bufferSource.getIndexBuffer();
			if (indexBuffer == null) return;
			GpuBuffer vertexBuffer = state.bufferSource.getVertexBuffer();
			if (vertexBuffer == null) return;

			state.dynamicUniform = RenderSystem.getDynamicUniforms().writeChunkSections(new DynamicUniforms.ChunkSectionInfo(
					state.levelRenderState.cameraRenderState.viewRotationMatrix,
					state.x,
					state.y,
					state.z,
					state.opacity,
					atlasView.getWidth(0),
					atlasView.getHeight(0)
			))[0];

			RenderPass.Draw<GpuBufferSlice> draw = new RenderPass.Draw<>(
					0,
					state.bufferSource.getVertexBuffer(),
					state.bufferSource.getIndexBuffer(),
					VertexFormat.IndexType.SHORT,
					0,
					(int) (state.bufferSource.getIndexBuffer().size() / VertexFormat.IndexType.SHORT.bytes),
					0,
					(slice, uploader) -> uploader.upload("ChunkSection", slice)
			);
			SECTION_DRAWS.add(new SectionDraw(draw, state));
		});
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
			//noinspection ConstantValue
			if (region == null) return;
			synchronized (STATES_TO_RENDER) {
				region.regionBakers.forEach(regionBaker -> {
					regionBaker.getBufferSource().close();
					STATES_TO_RENDER.remove(regionBaker.getRenderState());
				});
			}
			DIRTY_REGIONS.remove(pos);
			REGIONS.remove(pos);
		});
	}

	public static void clearRegions() {
		REGIONS.forEach((pos, _) -> removeRegion(pos));
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
	 */
	public void bake() {
		try {
			synchronized (this.regionBakers) {
				this.regionBakers.forEach(RegionBaker::bake);
			}
		} catch(Exception e) {
			BarricadeMod.LOG.error("Exception during BakedRegion baking", e);
		}
	}

	/**
	 * Upload this region to the GPU.
	 */
	public void upload() {
		try {
			this.regionBakers.forEach(baker -> baker.getBufferSource().upload());
		} catch(Exception e) {
			BarricadeMod.LOG.error("Exception during BakedRegion uploading", e);
		}

	}

	public record ExtractContext(LevelAccessor level, Player player, float deltaTick, LevelRenderState levelRenderState) {}

	/// a type of [MultiBufferSource] that is cached and has a future version which is where the new changes go yes
	public static class CachedMultiBufferSource implements MultiBufferSource, AutoCloseable {
		private final @Nullable CachedMultiBufferSource future;
		private @org.jspecify.annotations.Nullable BufferBuilder buffer;
		private @org.jspecify.annotations.Nullable ByteBufferBuilder byteBuffer;
		private @org.jspecify.annotations.Nullable MeshData meshData;
		private @org.jspecify.annotations.Nullable GpuBuffer vertexBuffer;
		private @org.jspecify.annotations.Nullable GpuBuffer indexBuffer;
		private final Identifier bakerLocation;

		public CachedMultiBufferSource(Identifier bakerLocation) {
			this(bakerLocation, ofFuture(bakerLocation));
		}

		private CachedMultiBufferSource(Identifier bakerLocation, @Nullable CachedMultiBufferSource future) {
			this.bakerLocation = bakerLocation.withPrefix("baker/");
			this.future = future;
		}

		private static CachedMultiBufferSource ofFuture(Identifier bakerLocation) {
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
			this.buffer = this.future.buffer;
			this.byteBuffer = this.future.byteBuffer;
			this.meshData = this.future.meshData;
			this.vertexBuffer = this.future.vertexBuffer;
			this.indexBuffer = this.future.indexBuffer;
			// clear the future CMBS
			this.future.buffer = null;
			this.future.byteBuffer = null;
			this.future.meshData = null;
			this.future.vertexBuffer = null;
			this.future.indexBuffer = null;
		}

		@Contract("null -> fail")
		private void assertNotFuture(@org.jspecify.annotations.Nullable CachedMultiBufferSource old) {
			if (old == null) {
				throw new IllegalStateException("Cannot get the old CachedMultiBufferSource of an old CachedMultiBufferSource");
			}
		}

		public VertexConsumer getBuffer() {
			this.assertNotFuture(this.future);
			if (this.future.buffer == null) {
				if (this.future.byteBuffer == null) {
					this.future.byteBuffer = new ByteBufferBuilder(BakedBarrierBlockRenderer.LAYER.bufferSize());
				}

				this.future.buffer = new BufferBuilder(
						this.future.byteBuffer,
						RENDER_PIPELINE.getVertexFormatMode(),
						RENDER_PIPELINE.getVertexFormat()
				);
			}

			return this.future.buffer;
		}

		@Override
		public VertexConsumer getBuffer(RenderType renderType) {
			throw new UnsupportedOperationException("lol no rendertype here buddy, use the other getBuffer method");
		}

		public void upload() {
			this.assertNotFuture(this.future);

			if (this.future.buffer == null) return;

			GpuDevice gpu = RenderSystem.getDevice();
			MeshData meshData = this.future.buffer.build();

			if (meshData == null) return;

			meshData.sortQuads(Objects.requireNonNull(this.future.byteBuffer, "CMBS ByteBufferBuilder is null"), VertexSorting.ORTHOGRAPHIC_Z);
			this.future.meshData = meshData;
			this.future.vertexBuffer = gpu.createBuffer(
					() -> this.bakerLocation + " Vertex Buffer",
					GpuBuffer.USAGE_VERTEX,
					meshData.vertexBuffer()
			);
			this.future.indexBuffer = gpu.createBuffer(
					() -> this.bakerLocation + " Index Buffer",
					GpuBuffer.USAGE_INDEX,
					Objects.requireNonNull(
							meshData.indexBuffer(),
							"Baked Region index buffer failed to build"
					)
			);

			// Move everything to this CMBS
			this.update();
		}

		public @org.jspecify.annotations.Nullable GpuBuffer getVertexBuffer() {
			return this.vertexBuffer;
		}

		public @Nullable GpuBuffer getIndexBuffer() {
			return this.indexBuffer;
		}

		public void flush() {
			if (this.byteBuffer != null) {
				this.byteBuffer.discard();
			}
			if (this.meshData != null) {
				this.meshData.close();
			}
			if (this.vertexBuffer != null) {
				this.vertexBuffer.close();
			}
			if (this.indexBuffer != null) {
				this.indexBuffer.close();
			}
			this.buffer = null;
			this.meshData = null;
			this.vertexBuffer = null;
			this.indexBuffer = null;
		}

		private void closeSelf() {
			this.flush();
			if (this.byteBuffer != null) {
				this.byteBuffer.close();
			}
			this.byteBuffer = null;
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

	public record SectionDraw(RenderPass.Draw<GpuBufferSlice> draw, RegionBaker.RenderState state) {
	}
}

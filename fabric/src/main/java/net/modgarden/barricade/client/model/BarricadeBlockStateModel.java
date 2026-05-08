package net.modgarden.barricade.client.model;

import static net.modgarden.barricade.BarricadeMod.id;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.attachment.BarricadePalette;
import net.modgarden.barricade.attachment.ModAttachments;
import net.modgarden.barricade.block.BarricadeBlock;
import net.modgarden.barricade.data.BarricadeData;
import net.modgarden.barricade.mixin.client.Accessor_LevelSlice;
import net.modgarden.barricade.mixin.client.Accessor_RenderSectionRegion;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;

import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;

public class BarricadeBlockStateModel implements BlockStateModel {
	private final Material.Baked barrier;

	public BarricadeBlockStateModel(Material.Baked barrier) {
		this.barrier = barrier;
	}

	@Override
	public void emitQuads(
			QuadEmitter emitter,
			BlockAndTintGetter level,
			BlockPos pos,
			BlockState state,
			RandomSource random,
			Predicate<@Nullable Direction> cullTest
	) {
		if (!(state.getBlock() instanceof BarricadeBlock block)) {
			for (Direction direction : Direction.values()) {
				if (cullTest.test(direction)) continue;

				emitter
						.square(direction, 0, 0, 1, 1, 0)
						.materialBake(
								this.barrier,
								MutableQuadView.BAKE_LOCK_UV
						)
						.emit();
			}

			return;
		}

		// TODO: reduce object churn?
		//  what we should do here is hardcode the blocked directions to each direction block state
		Set<Direction> blockedDirections = block.directions(state).directions();

		// sometimes they'll try to throw us an empty BlockAndTintGetter, and to that I say "no"
		if (!(level instanceof ClientLevel) && !(level instanceof RenderSectionRegion) && !(BarricadeMod.SODIUM && level instanceof LevelSlice)) {
			for (Direction direction : blockedDirections) {
				if (cullTest.test(direction)) continue;

				emitter
						.square(direction, 0, 0, 1, 1, 0)
						.materialBake(
								this.barrier,
								MutableQuadView.BAKE_LOCK_UV
						)
						.emit();
			}

			return;
		}

		ClientLevel clientLevel;

		if (level instanceof RenderSectionRegion region) {
			clientLevel = ((Accessor_RenderSectionRegion) region).barricade$getLevel();
		} else if (BarricadeMod.SODIUM && level instanceof LevelSlice levelSlice) {
			clientLevel = ((Accessor_LevelSlice) (Object) levelSlice).barricade$getLevel();
		} else {
			BarricadeMod.LOG.error("Unsupported renderer detected! Only Vanilla and Sodium are supported. Expect visual bugs.");

			return;
		}

		LevelChunk chunk = clientLevel.getChunkAt(pos);

		if (!chunk.hasAttached(ModAttachments.BARRICADE_PALETTE)) {
			BarricadeMod.LOG.error("No attached Barricade Palette found @ {}", pos);
			return;
		}

		Int2ObjectMap<BarricadePalette> palettes = chunk.getAttachedOrThrow(ModAttachments.BARRICADE_PALETTE);
		BarricadePalette palette = palettes.get(chunk.getSectionIndex(pos.getY()));

		//noinspection ConstantValue // This may be null
		if (palette == null) {
			BarricadeMod.LOG.error("No attached Barricade Palette found @ {}", pos);

			palette = new BarricadePalette(new PalettedContainer<>(BarricadeData.UNKNOWN_HOLDER, BarricadeData.STRATEGY));
		}

		BarricadeData barricadeData = palette.palettedContainer().get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15).value();
		TextureAtlas blockAtlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(QuadAtlas.BLOCK.getId());
		Identifier iconId = barricadeData.icon().orElse(null);
		var icon = iconId != null ? new Material.Baked(blockAtlas.getSprite(iconId), false) : null;

		for (Direction direction : blockedDirections) {
			if (cullTest.test(direction)) continue;

			emitter
					.square(direction, 0, 0, 1, 1, 0)
					.materialBake(
							this.barrier,
							MutableQuadView.BAKE_LOCK_UV
					)
					.emit();

			if (icon != null) {
					emitter
							.square(direction, 0, 0, 1, 1, 0)
							.materialBake(
									icon,
									MutableQuadView.BAKE_LOCK_UV
							)
							.emit();
			}
		}
	}

	@Deprecated
	@Override
	public void collectParts(
			RandomSource random,
			List<BlockStateModelPart> output
	) {
	}

	@Override
	public Material.Baked particleMaterial() {
		return this.barrier;
	}

	@Override
	public @BakedQuad.MaterialFlags int materialFlags() {
		return 0;
	}

	public static class Unbaked implements BlockStateModel.UnbakedRoot {
		@Override
		public BlockStateModel bake(
				BlockState blockState,
				ModelBaker modelBakery
		) {
			return new BarricadeBlockStateModel(
					modelBakery.materials().get(
							new Material(id("block/barrier"), false),
							() -> "Barricade Barrier"
					)
			);
		}

		@Override
		public Object visualEqualityGroup(BlockState blockState) {
			return this;
		}

		@Override
		public void resolveDependencies(Resolver resolver) {
		}
	}
}

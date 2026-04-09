package net.modgarden.barricade.client.model;

import static net.modgarden.barricade.BarricadeMod.id;

import java.util.List;
import java.util.function.Predicate;

import net.modgarden.barricade.data.BlockedDirections;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.model.MeshQuadCollection;

public class BarricadeBlockStateModel implements BlockStateModel {
	private final Material.Baked barrier;
	private final Material.Baked icon;
	private final BlockedDirections blockedDirections;

	public BarricadeBlockStateModel(Identifier icon, BlockedDirections blockedDirections) {
		TextureAtlas blockAtlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(QuadAtlas.BLOCK.getId());
		this.barrier = new Material.Baked(blockAtlas.getSprite(id("block/barrier")), false);
		this.icon = new Material.Baked(blockAtlas.getSprite(icon), false);
		this.blockedDirections = blockedDirections;
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
		for (Direction direction : this.blockedDirections.directions()) {
			if (cullTest.test(direction)) continue;

			emitter
					.square(direction, 0, 0, 1, 1, 0)
					.materialBake(
							this.barrier,
							MutableQuadView.BAKE_LOCK_UV
					)
					.emit()
					.square(direction, 0, 0, 1, 1, 0)
					.materialBake(
							this.icon,
							MutableQuadView.BAKE_LOCK_UV
					)
					.emit();
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
		return this.icon;
	}

	@Override
	public @BakedQuad.MaterialFlags int materialFlags() {
		return 0;
	}
}

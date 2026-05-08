package net.modgarden.barricade.client.model.item;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.modgarden.barricade.data.BarricadeData;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableMesh;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;

public record BarricadeItemModel(BarricadeBakery bakery) implements SpecialModelRenderer<BarricadeData> {
	@Override
	public void submit(
			@Nullable BarricadeData argument,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			int lightCoords,
			int overlayCoords,
			boolean hasFoil,
			int outlineColor
	) {
		if (argument == null) {
			argument = BarricadeData.UNKNOWN;
		}

		Material.Baked higher;
		Material.Baked lower;

		if (argument.itemBackground().isPresent()) {
			higher = bakery.getOrBake(argument.itemBackground().get());
		} else {
			higher = bakery.getOrBake(BarricadeItemTextures.BARRIER_TEXTURE);
		}

		if (argument.itemIcon().isPresent()) {
			lower = bakery.getOrBake(argument.itemIcon().get());
		} else if (argument.icon().isPresent()) {
			lower = bakery.getOrBake(argument.icon().get());
		} else {
			lower = null;
		}

		MutableMesh mesh = Renderer.get().mutableMesh();
		QuadEmitter emitter = mesh.emitter();

		emitter.square(Direction.SOUTH, 0, 0, 1, 1, 0.5f).materialBake(higher, MutableQuadView.BAKE_LOCK_UV).emit().square(Direction.NORTH, 0, 0, 1, 1, 0.5f).materialBake(higher, MutableQuadView.BAKE_LOCK_UV).emit();

		if (lower != null) {
			emitter.square(Direction.SOUTH, 0, 0, 1, 1, 0.5f).materialBake(lower, MutableQuadView.BAKE_LOCK_UV).emit().square(Direction.NORTH, 0, 0, 1, 1, 0.5f).materialBake(lower, MutableQuadView.BAKE_LOCK_UV).emit();
		}

		submitNodeCollector.order(0).submitItem(poseStack, ItemDisplayContext.NONE, lightCoords, overlayCoords, outlineColor, new int[0], List.of(), mesh, hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE);
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
//		output.accept(new Vector3f());
	}

	@Override
	public BarricadeData extractArgument(ItemStack stack) {
		Holder<BarricadeData> holder = stack.getOrDefault(BarricadeComponents.BARRICADE, BarricadeData.UNKNOWN_HOLDER);

		if (!holder.isBound()) {
			return null;
		}

		return holder.value();
	}

	public record Unbaked(
			ModelModifier.BeforeBakeItem.Context context1) implements SpecialModelRenderer.Unbaked<BarricadeData> {
		@Override
		public @NonNull SpecialModelRenderer<BarricadeData> bake(BakingContext context) {
			MaterialBaker materials = context1.bakingContext().blockModelBaker().materials();
			var bakery = new BarricadeBakery(materials);
			return new BarricadeItemModel(bakery);
		}

		@Override
		public MapCodec<? extends SpecialModelRenderer.Unbaked<BarricadeData>> type() {
			return MapCodec.unit(this);
		}
	}
}

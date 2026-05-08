package net.modgarden.barricade.client.render;

import static net.modgarden.barricade.BarricadeMod.id;

import java.util.Optional;

import net.modgarden.barricade.client.model.BarricadeBlockStateModel;
import net.modgarden.barricade.client.model.item.BarricadeItemModel;
import net.modgarden.barricade.registry.BarricadeBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public final class BarricadeRendering {
	private BarricadeRendering() {
	}

	public static void initialize() {
		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.registerBlockStateResolver(BarricadeBlocks.BARRICADE.get(), BarricadeRendering::resolveBlockStates);
			pluginContext.registerBlockStateResolver(Blocks.BARRIER, BarricadeRendering::resolveBlockStates);
			pluginContext.modifyItemModelBeforeBake().register((model, context1) -> {
				if (!context1.itemId().equals(id("barricade"))) return model;

				return new SpecialModelWrapper.Unbaked(
						Identifier.withDefaultNamespace("item/generated"), Optional.empty(), new BarricadeItemModel.Unbaked(context1));
			});
		});
	}

	public static void reloadBarriers() {
		LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
		levelRenderer.allChanged();
	}

	private static void resolveBlockStates(BlockStateResolver.Context context) {
		BarricadeBlockStateModel.Unbaked model = new BarricadeBlockStateModel.Unbaked();

		for (BlockState state : context.block().getStateDefinition().getPossibleStates()) {
			context.setModel(state, model);
		}
	}
}

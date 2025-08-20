package net.modgarden.barricade.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.client.BarricadeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public class ModelManagerMixin {
	@Inject(method = "loadModels", at = @At("HEAD"))
	private static void barricade$captureModelBakery(
			Map<ResourceLocation, AtlasSet.StitchResult> stitchResults,
			ModelBakery modelBakery,
			Object2IntMap<BlockState> modelGroups,
			EntityModelSet entityModelSet,
			SpecialBlockModelRenderer specialBlockModelRenderer,
			Executor executor,
			CallbackInfoReturnable<CompletableFuture<ModelManager.ReloadState>> cir
	) {
		BarricadeClient.setModelBakery(modelBakery);
	}
}

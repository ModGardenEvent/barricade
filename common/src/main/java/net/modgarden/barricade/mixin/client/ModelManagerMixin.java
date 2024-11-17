package net.modgarden.barricade.mixin.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.model.OperatorBakedModelAccess;
import net.modgarden.barricade.client.model.OperatorUnbakedModel;
import net.modgarden.barricade.client.util.BarrierRenderUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.Reader;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(ModelManager.class)
public class ModelManagerMixin {
    @Inject(method = "loadModels", at = @At("HEAD"))
    private void barricade$captureModelBakery(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, ModelBakery modelBakery, CallbackInfoReturnable<ModelManager.ReloadState> cir) {
        BarricadeClient.setModelBakery(modelBakery);
    }

    @ModifyExpressionValue(method = { "method_45899", "lambda$loadBlockModels$10" }, at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;"))
    private static Set<Map.Entry<ResourceLocation, Resource>> barricade$fuckOffAxiom(Set<Map.Entry<ResourceLocation, Resource>> value) {
        return value.stream().filter(entry -> !BarrierRenderUtils.isLightBlockId(entry.getKey()) || !entry.getValue().sourcePackId().equals("axiom")).collect(Collectors.toSet());
    }

    @Inject(method = { "method_45898", "lambda$loadBlockModels$8" }, at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;of(Ljava/lang/Object;Ljava/lang/Object;)Lcom/mojang/datafixers/util/Pair;"))
    private static void barricade$addToModelCache(Map.Entry<ResourceLocation, Resource> entry, CallbackInfoReturnable<Pair> cir) {
        try {
            JsonElement element = JsonParser.parseReader(entry.getValue().openAsReader());
            if (element.isJsonObject() && GsonHelper.getAsString(element.getAsJsonObject(), "loader").equals(OperatorUnbakedModel.Deserializer.ID.toString()))
                Barricade.addToOperatorModelCache(entry.getKey());
            else
                Barricade.removeFromOperatorModelCache(entry.getKey());
        } catch (Exception ignored) {}
    }
}

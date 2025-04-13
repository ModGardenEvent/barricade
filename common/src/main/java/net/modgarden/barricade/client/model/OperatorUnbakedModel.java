package net.modgarden.barricade.client.model;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Function;

public class OperatorUnbakedModel implements UnbakedModel {
	protected final UnbakedModel sourceModel;
	protected final Either<ResourceLocation, ResourceKey<Block>> operatorBlocks;

	public OperatorUnbakedModel(
			UnbakedModel model,
			Either<ResourceLocation, ResourceKey<Block>> operatorBlocks) {
		this.sourceModel = model;
		this.operatorBlocks = operatorBlocks;
	}

	@Override
	public @NotNull Collection<ResourceLocation> getDependencies() {
		return sourceModel.getDependencies();
	}

	@Override
	public void resolveParents(@NotNull Function<ResourceLocation, UnbakedModel> resolver) {
		sourceModel.resolveParents(resolver);
	}

	@NotNull
	@Override
	public BakedModel bake(@NotNull ModelBaker modelBaker, @NotNull Function<Material, TextureAtlasSprite> textureGetter, @NotNull ModelState modelState) {
		return BarricadeClient.getHelper().createCreativeOnlyModel(sourceModel.bake(modelBaker, textureGetter, modelState), operatorBlocks);
	}

	public static class Deserializer implements JsonDeserializer<OperatorUnbakedModel> {
		public static final ResourceLocation ID = Barricade.asResource("operator");

		public OperatorUnbakedModel deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
			if (!json.isJsonObject())
				throw new JsonParseException("Could not deserialize non JsonObject 'barricade:operator' model.");

			JsonObject jsonObject = json.getAsJsonObject();

			BlockModel model = BlockModel.fromString(jsonObject.get("model").toString());

			if (!jsonObject.has("operator_blocks"))
				throw new JsonParseException("Could not find 'operator_blocks' field from 'barricade:operator' model.");

			Either<ResourceLocation, ResourceKey<Block>> either = getEither(GsonHelper.getAsString(jsonObject, "operator_blocks"));

			return new OperatorUnbakedModel(model, either);
		}

		private static Either<ResourceLocation, ResourceKey<Block>> getEither(String id) throws JsonParseException {
			Either<ResourceLocation, ResourceKey<Block>> either;
			try {
				if (id.startsWith("#"))
					either = Either.left(ResourceLocation.read(id.substring(1)).getOrThrow());
				else
					either = Either.right(ResourceKey.create(Registries.BLOCK, ResourceLocation.read(id).getOrThrow()));
			} catch (Exception ex) {
				throw new JsonParseException("Failed to parse 'block' field from 'barricade:operator' model. Must be either a block id or an operator block tag.", ex);
			}
			return either;
		}
	}
}

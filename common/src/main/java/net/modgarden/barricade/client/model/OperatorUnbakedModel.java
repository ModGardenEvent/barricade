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
import net.minecraft.world.item.Item;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Function;

public class OperatorUnbakedModel implements UnbakedModel {
    protected final UnbakedModel sourceModel;
    protected final Either<ResourceLocation, ResourceKey<Item>> requiredItem;

    public OperatorUnbakedModel(
            UnbakedModel model,
            Either<ResourceLocation, ResourceKey<Item>> requiredItem) {
        this.sourceModel = model;
        this.requiredItem = requiredItem;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return sourceModel.getDependencies();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {
        sourceModel.resolveParents(resolver);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> textureGetter, ModelState modelState) {
        return BarricadeClient.getHelper().createCreativeOnlyModel(sourceModel.bake(modelBaker, textureGetter, modelState), requiredItem);
    }

    public static class Deserializer implements JsonDeserializer<OperatorUnbakedModel> {
        public static final ResourceLocation ID = Barricade.asResource("operator");

        public OperatorUnbakedModel deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject())
                throw new JsonParseException("Cannot deserialize non JsonObject 'barricade:operator' model.");

            JsonObject jsonObject = json.getAsJsonObject();

            BlockModel model = BlockModel.fromString(jsonObject.get("model").toString());

            if (!jsonObject.has("required_item"))
                throw new JsonParseException("Cannot get 'required_item' field from 'barricade:operator' model.");

            Either<ResourceLocation, ResourceKey<Item>> either = getEither(GsonHelper.getAsString(jsonObject, "required_item"));

            return new OperatorUnbakedModel(model, either);
        }

        private static Either<ResourceLocation, ResourceKey<Item>> getEither(String id) throws JsonParseException {
            Either<ResourceLocation, ResourceKey<Item>> either;
            try {
                if (id.startsWith("#"))
                    either = Either.left(ResourceLocation.read(id.substring(1)).getOrThrow());
                else
                    either = Either.right(ResourceKey.create(Registries.ITEM, ResourceLocation.read(id).getOrThrow()));
            } catch (Exception ex) {
                throw new JsonParseException("Failed to parse 'block' field from 'barricade:operator' model. Must be either a block id or a block tag.", ex);
            }
            return either;
        }
    }
}

package net.modgarden.barricade.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Function;

public class CreativeOnlyUnbakedModel implements UnbakedModel {
    protected final UnbakedModel sourceModel;
    protected final Either<TagKey<Item>, ResourceKey<Item>> requiredItem;

    public CreativeOnlyUnbakedModel(
            UnbakedModel model,
            Either<TagKey<Item>, ResourceKey<Item>> requiredItem) {
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

    public static class Deserializer implements JsonDeserializer<CreativeOnlyUnbakedModel> {
        public static final ResourceLocation ID = Barricade.asResource("operator");

        public CreativeOnlyUnbakedModel deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject())
                throw new JsonParseException("Cannot deserialize non JsonObject 'barricade:operator' model.");

            JsonObject jsonObject = json.getAsJsonObject();

            BlockModel model = BlockModel.fromString(jsonObject.get("model").toString());

            if (!jsonObject.has("required_item"))
                throw new JsonParseException("Cannot get 'required_item' field from 'barricade:operator' model.");

            Either<TagKey<Item>, ResourceKey<Item>> either = getEither(GsonHelper.getAsString(jsonObject, "required_item"));

            return new CreativeOnlyUnbakedModel(model, either);
        }

        private static Either<TagKey<Item>, ResourceKey<Item>> getEither(String id) throws JsonParseException {
            Either<TagKey<Item>, ResourceKey<Item>> either;
            try {
                if (id.startsWith("#"))
                    either = Either.left(TagKey.create(Registries.ITEM, ResourceLocation.read(id.substring(1)).getOrThrow()));
                else
                    either = Either.right(ResourceKey.create(Registries.ITEM, ResourceLocation.read(id).getOrThrow()));
            } catch (Exception ex) {
                throw new JsonParseException("Failed to parse 'block' field from 'barricade:operator' model. Must be either a block id or a block tag.", ex);
            }
            return either;
        }
    }
}

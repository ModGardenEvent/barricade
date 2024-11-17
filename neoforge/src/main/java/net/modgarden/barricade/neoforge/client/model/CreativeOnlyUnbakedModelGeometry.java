package net.modgarden.barricade.neoforge.client.model;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.modgarden.barricade.client.model.OperatorUnbakedModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class CreativeOnlyUnbakedModelGeometry implements IUnbakedGeometry<CreativeOnlyUnbakedModelGeometry> {
    private final OperatorUnbakedModel model;

    public CreativeOnlyUnbakedModelGeometry(OperatorUnbakedModel model) {
        this.model = model;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        model.resolveParents(baker::getModel);
        return model.bake(baker, spriteGetter, modelState);
    }
}
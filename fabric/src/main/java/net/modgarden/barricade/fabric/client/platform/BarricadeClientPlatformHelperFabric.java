package net.modgarden.barricade.fabric.client.platform;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.modgarden.barricade.client.platform.BarricadeClientPlatformHelper;
import net.modgarden.barricade.fabric.client.model.CreativeOnlyBakedModel;

import java.util.Collection;

public class BarricadeClientPlatformHelperFabric implements BarricadeClientPlatformHelper {
    @Override
    public Collection<BlockElement> fixSeamsOnNeoForge(Collection<BlockElement> collection, TextureAtlasSprite textureAtlasSprite) {
        return collection;
    }

    @Override
    public BlockModel.Deserializer getBlockModelDeserializer() {
        return new BlockModel.Deserializer();
    }

    @Override
    public BakedModel createCreativeOnlyModel(BakedModel model, Either<ResourceLocation, ResourceKey<Item>> requiredItem) {
        return new CreativeOnlyBakedModel(model, requiredItem);
    }

}
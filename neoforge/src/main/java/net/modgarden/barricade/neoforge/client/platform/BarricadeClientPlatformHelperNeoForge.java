package net.modgarden.barricade.neoforge.client.platform;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.client.platform.BarricadeClientPlatformHelper;
import net.modgarden.barricade.neoforge.client.model.CreativeOnlyBakedModel;
import net.neoforged.neoforge.client.model.ExtendedBlockModelDeserializer;

import java.util.Collection;

public class BarricadeClientPlatformHelperNeoForge implements BarricadeClientPlatformHelper {
    @Override
    public Collection<BlockElement> fixSeamsOnNeoForge(Collection<BlockElement> collection, TextureAtlasSprite textureAtlasSprite) {
        return collection;
    }

    @Override
    public BlockModel.Deserializer getBlockModelDeserializer() {
        return new ExtendedBlockModelDeserializer();
    }

    @Override
    public BakedModel createCreativeOnlyModel(BakedModel model, Either<TagKey<Block>, ResourceKey<Block>> tagOrBlock) {
        return new CreativeOnlyBakedModel(model, tagOrBlock);
    }

}
package net.modgarden.barricade.client.platform;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

public interface BarricadeClientPlatformHelper {

    Collection<BlockElement> fixSeamsOnNeoForge(Collection<BlockElement> collection, TextureAtlasSprite textureAtlasSprite);

    BlockModel.Deserializer getBlockModelDeserializer();

    BakedModel createCreativeOnlyModel(BakedModel model, Either<TagKey<Block>, ResourceKey<Block>> tagOrBlock);

}

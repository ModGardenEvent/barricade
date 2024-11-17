package net.modgarden.barricade.client.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;

public interface BarricadeClientPlatformHelper {

    Collection<BlockElement> fixSeamsOnNeoForge(Collection<BlockElement> collection, TextureAtlasSprite textureAtlasSprite);

    void tessellateBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, RandomSource random, long seed, int packedOverlay);

    BakedModel createCreativeOnlyModel(BakedModel model, Either<ResourceLocation, ResourceKey<Item>> requiredItem);

}

package net.modgarden.barricade.neoforge.client.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.model.CreativeOnlyBakedModelAccess;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CreativeOnlyBakedModel extends BakedModelWrapper<BakedModel> implements CreativeOnlyBakedModelAccess {
    private final Either<TagKey<Block>, ResourceKey<Block>> tagOrBlock;

    public CreativeOnlyBakedModel(BakedModel model, Either<TagKey<Block>, ResourceKey<Block>> tagOrBlock) {
        super(model);
        this.tagOrBlock = tagOrBlock;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        if (BarricadeClient.terrainContext.get() != null && (Minecraft.getInstance().gameMode.getPlayerMode() != GameType.CREATIVE || !Minecraft.getInstance().player.isHolding(stack -> stack.getItem() instanceof BlockItem blockItem && tagOrBlock.map(tagKey -> blockItem.getBlock().builtInRegistryHolder().is(tagKey), blockKey -> blockItem.getBlock().builtInRegistryHolder().is(blockKey)))))
            return Collections.emptyList();

        return originalModel.getQuads(state, side, rand, extraData, renderType);
    }

    @Override
    public Either<TagKey<Block>, ResourceKey<Block>> tagOrBlock() {
        return tagOrBlock;
    }
}

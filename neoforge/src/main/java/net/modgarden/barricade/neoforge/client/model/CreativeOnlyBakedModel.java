package net.modgarden.barricade.neoforge.client.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.model.OperatorBakedModelAccess;
import net.modgarden.barricade.client.util.OperatorBlockPseudoTag;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class CreativeOnlyBakedModel extends BakedModelWrapper<BakedModel> implements OperatorBakedModelAccess {
    private final Either<ResourceLocation, ResourceKey<Block>> operatorBlocks;
    private OperatorBlockPseudoTag cachedPseudoTag;
    private static final ModelProperty<Unit> IS_TERRAIN = new ModelProperty<>();

    public CreativeOnlyBakedModel(BakedModel model, Either<ResourceLocation, ResourceKey<Block>> operatorBlocks) {
        super(model);
        this.operatorBlocks = operatorBlocks;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        if (!Minecraft.getInstance().player.getAbilities().instabuild ||
                !BarricadeClient.CONFIG.get().everythingVisible() && BarricadeClient.CONFIG.get().visibleBlocks().stream().noneMatch(either ->
                        either.map(tag ->
                                        OperatorBlockPseudoTag.Registry.get(tag).blocks().contains(state.getBlockHolder()),
                                key -> state.getBlockHolder().is(key)
                        )
                ) && !Minecraft.getInstance().player.isHolding(stack -> requiredBlock().map(tag -> {
                    if (stack.getItem() instanceof BlockItem blockItem)
                        return tag.blocks().contains(blockItem.getBlock().builtInRegistryHolder());
                    return false;
                }, key -> {
                    if (stack.getItem() instanceof BlockItem blockItem)
                        return blockItem.getBlock().builtInRegistryHolder().is(key);
                    return false;
                })))
            return Collections.emptyList();

        return originalModel.getQuads(state, side, rand, extraData, renderType);
    }

    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        return ModelData.builder().with(IS_TERRAIN, Unit.INSTANCE).build();
    }

    @Override
    public Either<OperatorBlockPseudoTag, ResourceKey<Block>> requiredBlock() {
        return operatorBlocks.mapBoth(id -> {
            if (cachedPseudoTag == null)
                cachedPseudoTag = OperatorBlockPseudoTag.Registry.get(id);
            return cachedPseudoTag;
        }, Function.identity());
    }
}

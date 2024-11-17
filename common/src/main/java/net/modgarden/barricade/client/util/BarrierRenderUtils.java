package net.modgarden.barricade.client.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.model.OperatorBakedModelAccess;
import net.modgarden.barricade.item.AdvancedBarrierBlockItem;
import net.modgarden.barricade.mixin.client.ClientChunkCacheAccessor;
import net.modgarden.barricade.mixin.client.ClientChunkCacheStorageAccessor;
import net.modgarden.barricade.mixin.client.LevelRendererInvoker;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BarrierRenderUtils {
    public static void refreshOperatorBlocks(ItemStack stack, ItemStack previousStack, ItemStack otherHandStack) {
        if (testOtherStack(stack, otherHandStack) || testOtherStack(previousStack, otherHandStack))
            return;

        if ((stack.getItem() instanceof AdvancedBarrierBlockItem && !OperatorItemPseudoTag.Registry.get(Barricade.asResource("barriers")).contains(previousStack.getItemHolder())) || (previousStack.getItem() instanceof AdvancedBarrierBlockItem && !OperatorItemPseudoTag.Registry.get(Barricade.asResource("barriers")).contains(stack.getItemHolder()))) {
            Either<OperatorItemPseudoTag, ResourceKey<Item>> current = null;
            Either<OperatorItemPseudoTag, ResourceKey<Item>> previous = null;
            if (stack.getItem() instanceof BlockItem blockItem) {
                BlockState state = previousStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
                if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model)
                    current = model.requiredItem();
            }

            if (previousStack.getItem() instanceof BlockItem blockItem) {
                BlockState state = previousStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
                if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model)
                    previous = model.requiredItem();
            }

            if (stack.getItem() instanceof AdvancedBarrierBlockItem)
                current = Either.left(OperatorItemPseudoTag.Registry.get(Barricade.asResource("barriers")));
            if (previousStack.getItem() instanceof AdvancedBarrierBlockItem)
                current = Either.left(OperatorItemPseudoTag.Registry.get(Barricade.asResource("barriers")));

            refreshOperatorSections(stack, previousStack, current, previous);
        } else if (stack.getItem() instanceof BlockItem blockItem && previousStack.getItem() instanceof BlockItem lastBlockItem) {
            BlockState state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            BlockState lastState = previousStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(lastBlockItem.getBlock().defaultBlockState());
            BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
            BakedModel previousModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(lastState);
            if (!(model instanceof OperatorBakedModelAccess) && (!(previousModel instanceof OperatorBakedModelAccess)))
                return;
            if ((model instanceof OperatorBakedModelAccess && !(previousModel instanceof OperatorBakedModelAccess)) || (!(model instanceof OperatorBakedModelAccess) && (previousModel instanceof OperatorBakedModelAccess)) || !((OperatorBakedModelAccess) model).requiredItem().equals(((OperatorBakedModelAccess) previousModel).requiredItem()))
                refreshOperatorSections(stack, previousStack, model instanceof OperatorBakedModelAccess operatorModel ? operatorModel.requiredItem() : null, previousModel instanceof OperatorBakedModelAccess operatorModel ? operatorModel.requiredItem() : null);
        } else if (stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model)
                refreshOperatorSections(stack, previousStack, model.requiredItem(), null);
        } else if (previousStack.getItem() instanceof BlockItem blockItem) {
            BlockState state = previousStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model)
                refreshOperatorSections(stack, previousStack, null, model.requiredItem());
        }
    }

    private static boolean testOtherStack(ItemStack stack, ItemStack otherHandStack) {
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(otherHandStack.getItem() instanceof BlockItem otherBlockItem))
            return false;
        BlockState state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
        BlockState otherState = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(otherBlockItem.getBlock().defaultBlockState());
        if (!(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model) || !(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(otherState) instanceof OperatorBakedModelAccess otherModel))
            return false;
        if (model.requiredItem().map(tag -> tag.contains(otherHandStack.getItemHolder()), key -> otherHandStack.getItemHolder().is(key)) || otherModel.requiredItem().map(tag -> tag.contains(stack.getItemHolder()), key -> stack.getItemHolder().is(key)))
            return true;
        return false;
    }

    public static void refreshAllOperatorBlocks() {
        Set<SectionPos> operatedSectionPos = new HashSet<>();
        var chunks = ((ClientChunkCacheStorageAccessor)(Object)((ClientChunkCacheAccessor) Minecraft.getInstance().level.getChunkSource()).getStorage()).getChunks();
        for (int i = 0; i < chunks.length(); ++i) {
            LevelChunk chunk = chunks.get(i);
            if (chunk == null)
                continue;
            chunk.findBlocks(state -> Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess, (pos, state) -> {
                setBlockDirty(pos, operatedSectionPos);
            });
        }
    }

    public static void refreshOperatorSections(ItemStack stack, ItemStack lastItemInHand, @Nullable Either<OperatorItemPseudoTag, ResourceKey<Item>> current, @Nullable Either<OperatorItemPseudoTag, ResourceKey<Item>> previous) {
        Set<SectionPos> operatedSectionPos = new HashSet<>();
        var chunks = ((ClientChunkCacheStorageAccessor)(Object)((ClientChunkCacheAccessor) Minecraft.getInstance().level.getChunkSource()).getStorage()).getChunks();
        for (int i = 0; i < chunks.length(); ++i) {
            LevelChunk chunk = chunks.get(i);
            if (chunk == null)
                continue;
            chunk.findBlocks(state -> Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model && (current != null && model.requiredItem().map(tag -> tag.contains(stack.getItemHolder()), key -> stack.getItemHolder().is(key)) || previous != null && model.requiredItem().map(tag -> tag.contains(lastItemInHand.getItemHolder()), key -> lastItemInHand.getItemHolder().is(key))), (pos, state) -> {
                setBlockDirty(pos, operatedSectionPos);
            });
        }
    }

    private static void setBlockDirty(BlockPos pos, Set<SectionPos> operatedSectionPos) {
        var sectionPos = SectionPos.of(pos);
        if (operatedSectionPos.contains(sectionPos))
            return;
        ((LevelRendererInvoker)Minecraft.getInstance().levelRenderer).barricade$invokeSetBlockDirty(pos,true);
        operatedSectionPos.add(sectionPos);
    }
}

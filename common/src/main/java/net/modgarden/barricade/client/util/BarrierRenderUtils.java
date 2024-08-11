package net.modgarden.barricade.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.modgarden.barricade.client.model.CreativeOnlyBakedModelAccess;
import net.modgarden.barricade.mixin.client.ClientChunkCacheAccessor;
import net.modgarden.barricade.mixin.client.ClientChunkCacheStorageAccessor;
import net.modgarden.barricade.mixin.client.LevelRendererInvoker;

import java.util.HashSet;
import java.util.Set;

public class BarrierRenderUtils {
    public static void refreshBarrierBlocks(ItemStack stack, ItemStack lastItemInMainHand) {
        if (stack.getItem() instanceof BlockItem blockItem && lastItemInMainHand.getItem() instanceof BlockItem lastBlockItem) {
            BlockState state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            BlockState lastState = lastItemInMainHand.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(lastBlockItem.getBlock().defaultBlockState());
            if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof CreativeOnlyBakedModelAccess ^ Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(lastState) instanceof CreativeOnlyBakedModelAccess)
                refreshBarrierSections();
        } else if (stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof CreativeOnlyBakedModelAccess)
                refreshBarrierSections();
        } else if (lastItemInMainHand.getItem() instanceof BlockItem blockItem) {
            BlockState state = lastItemInMainHand.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof CreativeOnlyBakedModelAccess)
                refreshBarrierSections();
        }
    }

    private static void refreshBarrierSections() {
        Set<SectionPos> operatedSectionPos = new HashSet<>();
        var chunks = ((ClientChunkCacheStorageAccessor)(Object)((ClientChunkCacheAccessor) Minecraft.getInstance().level.getChunkSource()).getStorage()).getChunks();
        for (int i = 0; i < chunks.length(); ++i) {
            LevelChunk chunk = chunks.get(i);
            if (chunk == null)
                continue;
            chunk.findBlocks(state -> Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof CreativeOnlyBakedModelAccess, (pos, state) -> {
                SectionPos section = SectionPos.of(pos);
                if (operatedSectionPos.contains(section))
                    return;
                ((LevelRendererInvoker)Minecraft.getInstance().levelRenderer).invokeSetSectionDirty(section.getX(), section.getY(), section.getZ(), true);
                operatedSectionPos.add(section);
            });
        }
    }
}

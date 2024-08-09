package net.modgarden.barricade.neoforge.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.model.CreativeOnlyBakedModelAccess;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(SectionCompiler.class)
public class SectionCompilerMixin {
    @Shadow @Final private BlockRenderDispatcher blockRenderer;

    @Inject(method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void barricade$setTerrainContext(SectionPos sectionPos, RenderChunkRegion region, VertexSorting vertexSorting, SectionBufferBuilderPack sectionBufferBuilderPack, List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers, CallbackInfoReturnable<SectionCompiler.Results> cir, SectionCompiler.Results sectioncompiler$results, BlockPos blockpos, BlockPos blockpos1, VisGraph visgraph, PoseStack posestack, Map map, RandomSource randomsource, Iterator var13, BlockPos blockpos2, BlockState state) {
        if (blockRenderer.getBlockModel(state) instanceof CreativeOnlyBakedModelAccess)
            BarricadeClient.terrainContext.set(Unit.INSTANCE);
    }

    @Inject(method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = At.Shift.BY, by = 2))
    private void barricade$resetTerrainContext(SectionPos sectionPos, RenderChunkRegion region, VertexSorting vertexSorting, SectionBufferBuilderPack sectionBufferBuilderPack, List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers, CallbackInfoReturnable<SectionCompiler.Results> cir) {
        if (BarricadeClient.terrainContext.get() != null)
            BarricadeClient.terrainContext.remove();
    }
}

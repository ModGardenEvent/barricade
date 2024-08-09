package net.modgarden.barricade.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.modgarden.barricade.client.model.CreativeOnlyBakedModelAccess;
import net.modgarden.barricade.mixin.client.ClientChunkCacheAccessor;
import net.modgarden.barricade.mixin.client.ClientChunkCacheStorageAccessor;
import net.modgarden.barricade.mixin.client.LevelRendererInvoker;

import java.util.HashSet;
import java.util.Set;

public class BarrierRenderUtils {
    public static void renderSprite(TextureAtlasSprite sprite, PoseStack pose, VertexConsumer consumer) {
        PoseStack.Pose pose1 = pose.last();
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        RenderSystem.setShader(GameRenderer::getRendertypeCutoutShader);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        vertex(consumer, pose1, 1.0F, -1.0F, u1, v1);
        vertex(consumer, pose1, 1.0F, 1.0F, u1, v0);
        vertex(consumer, pose1, -1.0F, 1.0F, u0, v0);
        vertex(consumer, pose1, -1.0F, -1.0F, u0, v1);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float xOffset, float yOffset, float u, float v) {
        consumer.addVertex(pose, xOffset, yOffset, 0.0F)
                .setNormal(0.0F, 0.0F, 0.0F)
                .setColor(-1)
                .setUv(u, v)
                .setLight(15728880);
    }

    public static void reloadBarrierBlocks() {
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

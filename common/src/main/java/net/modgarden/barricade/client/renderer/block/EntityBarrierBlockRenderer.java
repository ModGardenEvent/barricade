package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.modgarden.barricade.block.entity.EntityBarrierBlockEntity;
import net.modgarden.barricade.component.BlockedEntitiesComponent;

public class EntityBarrierBlockRenderer implements BlockEntityRenderer<EntityBarrierBlockEntity> {
    private static final Object2BooleanArrayMap<ResourceLocation> SUCCESS_MAP = new Object2BooleanArrayMap<>();
    private static final ResourceLocation BARRIER_TEXTURE = ResourceLocation.withDefaultNamespace("item/barrier");

    private final TextureAtlas blockAtlas;

    public EntityBarrierBlockRenderer() {
        this.blockAtlas = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void render(EntityBarrierBlockEntity blockEntity, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (blockEntity.getBackTextureLocation() == null || Minecraft.getInstance().gameMode.getPlayerMode() != GameType.CREATIVE || !Minecraft.getInstance().player.isHolding(blockEntity::matches))
            return;

        if (!SUCCESS_MAP.containsKey(blockEntity.getBackTextureLocation()))
            storeSuccessMap(blockAtlas, blockEntity.getBackTextureLocation());

        ResourceLocation backLoc = !SUCCESS_MAP.containsKey(blockEntity.getBackTextureLocation()) || !SUCCESS_MAP.getBoolean(blockEntity.getBackTextureLocation()) ? BlockedEntitiesComponent.DEFAULT_TEXTURE_ID : blockEntity.getBackTextureLocation();
        pose.pushPose();
        pose.translate(0.5F, 0.5, 0.5F);
        pose.scale(0.5F, 0.5F, 0.5F);
        pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        renderSprite(blockAtlas.getSprite(backLoc), pose, buffer, packedLight);
        renderSprite(blockAtlas.getSprite(BARRIER_TEXTURE), pose, buffer, packedLight);
        pose.popPose();
    }

    private static void renderSprite(TextureAtlasSprite sprite, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        PoseStack.Pose pose1 = pose.last();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        RenderSystem.setShader(GameRenderer::getRendertypeCutoutShader);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        vertex(vertexConsumer, pose1, packedLight, 1.0F, -1.0F, u1, v1);
        vertex(vertexConsumer, pose1, packedLight, 1.0F, 1.0F, u1, v0);
        vertex(vertexConsumer, pose1, packedLight, -1.0F, 1.0F, u0, v0);
        vertex(vertexConsumer, pose1, packedLight, -1.0F, -1.0F, u0, v1);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float xOffset, float yOffset, float u, float v) {
        consumer.addVertex(pose, xOffset, yOffset, 0.0F)
                .setNormal(0.0F, 0.0F, 0.0F)
                .setColor(-1)
                .setUv(u, v)
                .setLight(packedLight);
    }

    private static void storeSuccessMap(TextureAtlas blockAtlas, ResourceLocation textureLocation) {
        try {
            blockAtlas.getSprite(textureLocation);
        } catch (IllegalStateException ex) {
            SUCCESS_MAP.put(textureLocation, false);
            return;
        }
        SUCCESS_MAP.put(textureLocation, true);
    }

    public static void clearSuccessMap() {
        SUCCESS_MAP.clear();
    }
}

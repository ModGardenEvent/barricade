package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.entity.DirectionalBarrierBlockEntity;
import net.modgarden.barricade.component.BlockedDirectionsComponent;

public class DirectionalBarrierBlockRenderer implements BlockEntityRenderer<DirectionalBarrierBlockEntity> {
    private static final ResourceLocation NO_BARRIER_TEXTURE = Barricade.asResource("item/barricade/no_barrier");
    private static final ResourceLocation BARRIER_TEXTURE = ResourceLocation.withDefaultNamespace("item/barrier");

    public static final ResourceLocation DIRECTION_UP_TEXTURE = Barricade.asResource("item/barricade/direction/up");
    public static final ResourceLocation DIRECTION_DOWN_TEXTURE = Barricade.asResource("item/barricade/direction/down");
    public static final ResourceLocation DIRECTION_NORTH_TEXTURE = Barricade.asResource("item/barricade/direction/north");
    public static final ResourceLocation DIRECTION_SOUTH_TEXTURE = Barricade.asResource("item/barricade/direction/south");
    public static final ResourceLocation DIRECTION_WEST_TEXTURE = Barricade.asResource("item/barricade/direction/west");
    public static final ResourceLocation DIRECTION_EAST_TEXTURE = Barricade.asResource("item/barricade/direction/east");


    private final TextureAtlas blockAtlas;

    public DirectionalBarrierBlockRenderer() {
        this.blockAtlas = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void render(DirectionalBarrierBlockEntity blockEntity, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (Minecraft.getInstance().gameMode.getPlayerMode() != GameType.CREATIVE || !Minecraft.getInstance().player.isHolding(blockEntity::matches) || Minecraft.getInstance().player.distanceToSqr(blockEntity.getBlockPos().getCenter()) > 2048)
            return;

        pose.pushPose();
        pose.translate(0.5F, 0.5, 0.5F);
        pose.scale(0.5F, 0.5F, 0.5F);
        pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        BlockedDirectionsComponent component = BlockedDirectionsComponent.fromBlockState(blockEntity.getBlockState());
        renderDirectionalBarrier(component, blockAtlas, blockEntity.getBlockPos(), pose, buffer, packedLight);
        pose.popPose();
    }

    public static void renderDirectionalBarrier(BlockedDirectionsComponent component, TextureAtlas blockAtlas, BlockPos pos, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        if (component.doesNotBlock())
            renderSprite(blockAtlas.getSprite(DirectionalBarrierBlockRenderer.NO_BARRIER_TEXTURE), pose, buffer, packedLight);
        else if (component.blocksAll())
            renderSprite(blockAtlas.getSprite(DirectionalBarrierBlockRenderer.BARRIER_TEXTURE), pose, buffer, packedLight);
        else {
            renderSprite(blockAtlas.getSprite(DirectionalBarrierBlockRenderer.NO_BARRIER_TEXTURE), pose, buffer, packedLight);
            Direction relativeHorizontal = getRelativeHorizontalDirectionToPlayer(pos);
            Direction relativeVertical = getRelativeVerticalDirectionToPlayer(pos);

            Direction relativeNorth;
            Direction relativeSouth;
            Direction relativeWest;
            Direction relativeEast;
            Direction relativeUp;
            Direction relativeDown;

            switch (relativeHorizontal) {
                case SOUTH -> {
                    relativeSouth = Direction.NORTH;
                    relativeNorth = Direction.SOUTH;
                    relativeWest = Direction.EAST;
                    relativeEast = Direction.WEST;
                }
                case WEST -> {
                    relativeSouth = Direction.EAST;
                    relativeNorth = Direction.WEST;
                    relativeWest = Direction.SOUTH;
                    relativeEast = Direction.NORTH;
                }
                case EAST -> {
                    relativeSouth = Direction.WEST;
                    relativeNorth = Direction.EAST;
                    relativeWest = Direction.NORTH;
                    relativeEast = Direction.SOUTH;
                }
                default -> {
                    relativeSouth = Direction.SOUTH;
                    relativeNorth = Direction.NORTH;
                    relativeWest = Direction.WEST;
                    relativeEast = Direction.EAST;
                }
            }

            switch (relativeVertical) {
                case UP -> {
                    Direction previousSouth = relativeSouth;
                    Direction previousNorth = relativeNorth;
                    relativeSouth = Direction.UP;
                    relativeNorth = Direction.DOWN;
                    relativeUp = previousSouth;
                    relativeDown = previousNorth;
                }
                case DOWN -> {
                    Direction previousFront = relativeSouth;
                    Direction previousBehind = relativeNorth;
                    relativeSouth = Direction.DOWN;
                    relativeNorth = Direction.UP;
                    relativeUp = previousFront;
                    relativeDown = previousBehind;
                }
                case null, default -> {
                    relativeUp = Direction.UP;
                    relativeDown = Direction.DOWN;
                }
            }

            if (component.blocks(relativeSouth))
                renderSprite(blockAtlas.getSprite(DirectionalBarrierBlockRenderer.DIRECTION_SOUTH_TEXTURE), pose, buffer, packedLight);
            else if (component.blocks(relativeNorth))
                renderSprite(blockAtlas.getSprite(DirectionalBarrierBlockRenderer.DIRECTION_NORTH_TEXTURE), pose, buffer, packedLight);

            if (component.blocks(relativeEast))
                renderSprite(blockAtlas.getSprite(DirectionalBarrierBlockRenderer.DIRECTION_EAST_TEXTURE), pose, buffer, packedLight);

            if (component.blocks(relativeWest))
                renderSprite(blockAtlas.getSprite(DirectionalBarrierBlockRenderer.DIRECTION_WEST_TEXTURE), pose, buffer, packedLight);

            if (component.blocks(relativeUp))
                renderSprite(blockAtlas.getSprite(DirectionalBarrierBlockRenderer.DIRECTION_UP_TEXTURE), pose, buffer, packedLight);

            if (component.blocks(relativeDown))
                renderSprite(blockAtlas.getSprite(DirectionalBarrierBlockRenderer.DIRECTION_DOWN_TEXTURE), pose, buffer, packedLight);
        }
    }

    private static Direction getRelativeHorizontalDirectionToPlayer(BlockPos pos) {
        Player player = Minecraft.getInstance().player;
        return player.getDirection();
    }

    private static Direction getRelativeVerticalDirectionToPlayer(BlockPos pos) {
        Player player = Minecraft.getInstance().player;

        if (player == null)
            return null;

        Vec3 direction = new Vec3(0, 1, 0);
        double dot = direction.dot(player.position().subtract(pos.getCenter()));

        if (dot >= (0.5 * (player.position().subtract(pos.getCenter()).length())))
            return Direction.UP;

        if (dot <= (-0.9 * (player.position().subtract(pos.getCenter()).length())))
            return Direction.DOWN;

        return null;
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
}
package net.modgarden.barricade.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.util.AdvancedBarrierComponents;
import net.modgarden.barricade.client.model.AdvancedBarrierBlockUnbakedModel;
import net.modgarden.barricade.registry.BarricadeTags;

import java.util.HashMap;
import java.util.Map;

public class AdvancedBarrierBlockRenderer implements BlockEntityRenderer<AdvancedBarrierBlockEntity> {
    private static final Map<AdvancedBarrierComponents, BakedModel> MODEL_MAP = new HashMap<>();

    @Override
    @SuppressWarnings("ConstantConditions")
    public void render(AdvancedBarrierBlockEntity blockEntity, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!Barricade.isOperatorModel(Blocks.BARRIER.defaultBlockState()) || !Minecraft.getInstance().player.canUseGameMasterBlocks() || !Minecraft.getInstance().player.isHolding(stack -> stack.is(BarricadeTags.ItemTags.BARRIERS)))
            return;

        AdvancedBarrierComponents components = new AdvancedBarrierComponents(blockEntity.getBlockedEntities(), blockEntity.getBlockedDirections());

        BarricadeClient.getHelper().tessellateBlock(
                Minecraft.getInstance().level,
                MODEL_MAP.computeIfAbsent(components, AdvancedBarrierBlockRenderer::createModel),
                blockEntity.getBlockState(),
                blockEntity.getBlockPos(),
                pose,
                buffer.getBuffer(RenderType.cutout()),
                true,
                blockEntity.getLevel().random,
                blockEntity.getBlockState().getSeed(blockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY);
    }

    private static BakedModel createModel(AdvancedBarrierComponents components) {
        String variant = "";
        if (components.blockedEntities() != null)
            variant = components.blockedEntities().backTextureLocation() + "," + String.join(",", components.blockedEntities().entities().stream().map(either -> either.map(tagKey -> "#" + tagKey.location(), holder -> holder.unwrapKey().map(ResourceKey::location).orElse(ResourceLocation.withDefaultNamespace("null")).toString())).toList());

        if (components.blockedDirections() != null && !components.blockedDirections().doesNotBlock()) {
            variant = String.join(",", components.blockedDirections().directions().stream().map(Direction::getName).toList());
        }
        BlockModel blockModel = new AdvancedBarrierBlockUnbakedModel(components.blockedDirections(), components.blockedEntities());
        return blockModel
                .bake(
                        BarricadeClient.getModelBakery().new ModelBakerImpl((modelLocation, material) -> material.sprite(), new ModelResourceLocation(Barricade.asResource("advanced_barrier_item_renderer"), variant)),
                        Material::sprite,
                        BlockModelRotation.X0_Y0
                );
    }

    public static void clearModelMap() {
        MODEL_MAP.clear();
    }
}

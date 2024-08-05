package net.modgarden.barricade.client.renderer.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.renderer.block.DirectionalBarrierBlockRenderer;
import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.mixin.client.ModelBakeryAccessor;
import net.modgarden.barricade.registry.BarricadeComponents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectionalBarrierItemRenderer {
    private static final Map<BlockedDirectionsComponent, BakedModel> MODEL_MAP = new HashMap<>();
    public static final Map<Direction, ResourceLocation> DIRECTION_TO_MATERIAL_LOCATION = Map.of(
            Direction.DOWN, DirectionalBarrierBlockRenderer.DIRECTION_DOWN_TEXTURE,
            Direction.UP, DirectionalBarrierBlockRenderer.DIRECTION_UP_TEXTURE,
            Direction.SOUTH, DirectionalBarrierBlockRenderer.DIRECTION_SOUTH_TEXTURE,
            Direction.NORTH, DirectionalBarrierBlockRenderer.DIRECTION_NORTH_TEXTURE,
            Direction.EAST, DirectionalBarrierBlockRenderer.DIRECTION_EAST_TEXTURE,
            Direction.WEST, DirectionalBarrierBlockRenderer.DIRECTION_WEST_TEXTURE
    );

    public static void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        BakedModel model;
        if (stack.has(BarricadeComponents.BLOCKED_DIRECTIONS) && stack.get(BarricadeComponents.BLOCKED_DIRECTIONS).blocksAll())
            model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(Items.BARRIER);
        else
            model = MODEL_MAP.computeIfAbsent(stack.getOrDefault(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.EMPTY), DirectionalBarrierItemRenderer::createModel);

        if (model == null)
            return;

        boolean left = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        pose.translate(0.5F, 0.5F, 0.5F);

        boolean bl = context == ItemDisplayContext.GUI && !model.usesBlockLight();
        MultiBufferSource.BufferSource source = null;

        if (bl) {
            Lighting.setupForFlatItems();
            source = Minecraft.getInstance().renderBuffers().bufferSource();
        }

        Minecraft.getInstance().getItemRenderer().render(stack, context, left, pose, source == null ? buffer : source, light, overlay, model);

        if (bl) {
            source.endBatch();
            Lighting.setupFor3DItems();
        }
    }

    public static BakedModel createModel(BlockedDirectionsComponent component) {
        Map<String, Either<Material, String>> textureMap = new HashMap<>();
        textureMap.put("barricade_layer0", Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, DirectionalBarrierBlockRenderer.NO_BARRIER_TEXTURE)));

        int i = 1;
        for (Map.Entry<Direction, Boolean> entry : component.directionMap().object2BooleanEntrySet()) {
            if (entry.getValue()) {
                textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, DIRECTION_TO_MATERIAL_LOCATION.get(entry.getKey()))));
                ++i;
            }
        }
        String variant = String.join(",", component.directionMap().keySet().stream().map(Direction::getName).toList());

        BlockModel blockModel = new BlockModel(ResourceLocation.withDefaultNamespace("builtin/generated"), List.of(), textureMap, false, BlockModel.GuiLight.FRONT, Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(Items.BARRIER).getTransforms(), List.of());
        return ModelBakeryAccessor.getItemModelGenerator()
                .generateBlockModel(Material::sprite, blockModel)
                .bake(
                        BarricadeClient.getModelBakery().new ModelBakerImpl((modelLocation, material) -> material.sprite(), new ModelResourceLocation(Barricade.asResource("directional_barrier_block_renderer"), variant)),
                        blockModel,
                        Material::sprite,
                        BlockModelRotation.X0_Y0,
                        false
                );
    }

    public static void clearModelMap() {
        MODEL_MAP.clear();
    }
}

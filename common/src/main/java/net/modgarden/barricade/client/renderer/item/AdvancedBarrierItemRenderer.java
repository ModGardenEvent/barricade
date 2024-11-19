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
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.util.AdvancedBarrierModelValues;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.mixin.client.ModelBakeryAccessor;
import net.modgarden.barricade.registry.BarricadeComponents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedBarrierItemRenderer {
    private static final Map<AdvancedBarrierModelValues, BakedModel> MODEL_MAP = new HashMap<>();

    public static final ResourceLocation NO_BARRIER_TEXTURE = Barricade.asResource("item/barricade/no_barrier");
    public static final ResourceLocation BARRIER_TEXTURE = ResourceLocation.withDefaultNamespace("item/barrier");

    public static final ResourceLocation DIRECTION_UP_TEXTURE = Barricade.asResource("item/barricade/direction/up");
    public static final ResourceLocation DIRECTION_DOWN_TEXTURE = Barricade.asResource("item/barricade/direction/down");
    public static final ResourceLocation DIRECTION_NORTH_TEXTURE = Barricade.asResource("item/barricade/direction/north");
    public static final ResourceLocation DIRECTION_SOUTH_TEXTURE = Barricade.asResource("item/barricade/direction/south");
    public static final ResourceLocation DIRECTION_WEST_TEXTURE = Barricade.asResource("item/barricade/direction/west");
    public static final ResourceLocation DIRECTION_EAST_TEXTURE = Barricade.asResource("item/barricade/direction/east");

    public static final Map<Direction, ResourceLocation> DIRECTION_TO_MATERIAL_LOCATION = Map.of(
            Direction.DOWN, DIRECTION_DOWN_TEXTURE,
            Direction.UP, DIRECTION_UP_TEXTURE,
            Direction.SOUTH, DIRECTION_SOUTH_TEXTURE,
            Direction.NORTH, DIRECTION_NORTH_TEXTURE,
            Direction.EAST, DIRECTION_EAST_TEXTURE,
            Direction.WEST, DIRECTION_WEST_TEXTURE
    );

    public static void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        Holder<AdvancedBarrier> component = stack.getOrDefault(BarricadeComponents.ADVANCED_BARRIER, Holder.direct(AdvancedBarrier.DEFAULT));
        AdvancedBarrierModelValues key = new AdvancedBarrierModelValues(component.value().directions(), component.value().icon().orElse(null));

        BakedModel model = MODEL_MAP.computeIfAbsent(key, AdvancedBarrierItemRenderer::createModel);

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

    public static BakedModel createModel(AdvancedBarrierModelValues values) {
        Map<String, Either<Material, String>> textureMap = new HashMap<>();
        int i = 0;

        if (values.icon() != null) {
            textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, values.icon())));
            ++i;
        }

        if (values.directions().blocksAll()) {
            textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, BARRIER_TEXTURE)));
            ++i;
        } else if (values.directions().doesNotBlock()) {
            textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, NO_BARRIER_TEXTURE)));
            ++i;
        } else {
            textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, NO_BARRIER_TEXTURE)));
            ++i;

            for (Direction entry : values.directions().directions()) {
                textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, DIRECTION_TO_MATERIAL_LOCATION.get(entry))));
                ++i;
            }
        }

        BlockModel blockModel = new BlockModel(ResourceLocation.withDefaultNamespace("builtin/generated"), List.of(), textureMap, false, BlockModel.GuiLight.FRONT, Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(Items.BARRIER).getTransforms(), List.of());
        return ModelBakeryAccessor.getItemModelGenerator()
                .generateBlockModel(Material::sprite, blockModel)
                .bake(
                        BarricadeClient.getModelBakery().new ModelBakerImpl((modelLocation, material) -> material.sprite(), new ModelResourceLocation(Barricade.asResource("advanced_barrier_item_renderer"), values.getVariant())),
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

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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.util.AdvancedBarrierComponents;
import net.modgarden.barricade.mixin.client.ModelBakeryAccessor;
import net.modgarden.barricade.registry.BarricadeComponents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedBarrierItemRenderer {
    private static final Map<AdvancedBarrierComponents, BakedModel> MODEL_MAP = new HashMap<>();

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
        AdvancedBarrierComponents components = new AdvancedBarrierComponents(stack.get(BarricadeComponents.BLOCKED_ENTITIES), stack.get(BarricadeComponents.BLOCKED_DIRECTIONS));
        BakedModel model = MODEL_MAP.computeIfAbsent(components, AdvancedBarrierItemRenderer::createModel);

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

    public static BakedModel createModel(AdvancedBarrierComponents components) {
        Map<String, Either<Material, String>> textureMap = new HashMap<>();
        int i = 0;
        if (components.blockedEntities() != null) {
            textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, components.blockedEntities().backTextureLocation())));
            ++i;
        }

        if (components.blockedDirections() == null || components.blockedDirections().blocksAll()) {
            textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, BARRIER_TEXTURE)));
            ++i;
        } else if (!components.blockedDirections().blocksAll()) {
            textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, NO_BARRIER_TEXTURE)));
            ++i;
        } else {
            textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, NO_BARRIER_TEXTURE)));
            ++i;

            for (Map.Entry<Direction, Boolean> entry : components.blockedDirections().directionMap().object2BooleanEntrySet()) {
                if (entry.getValue()) {
                    textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, DIRECTION_TO_MATERIAL_LOCATION.get(entry.getKey()))));
                    ++i;
                }
            }
        }

        String variant = "";
        if (components.blockedEntities() != null)
            variant = components.blockedEntities().backTextureLocation() + "," + String.join(",", components.blockedEntities().entities().stream().map(either -> either.map(tagKey -> "#" + tagKey.location(), holder -> holder.unwrapKey().map(ResourceKey::location).orElse(ResourceLocation.withDefaultNamespace("null")).toString())).toList());

        if (components.blockedDirections() != null && !components.blockedDirections().doesNotBlock()) {
            if (!variant.isEmpty())
                variant = variant + ",";
            variant = String.join(",", components.blockedDirections().directionMap().keySet().stream().map(Direction::getName).toList());
        }
        BlockModel blockModel = new BlockModel(ResourceLocation.withDefaultNamespace("builtin/generated"), List.of(), textureMap, false, BlockModel.GuiLight.FRONT, Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(Items.BARRIER).getTransforms(), List.of());
        return ModelBakeryAccessor.getItemModelGenerator()
                .generateBlockModel(Material::sprite, blockModel)
                .bake(
                        BarricadeClient.getModelBakery().new ModelBakerImpl((modelLocation, material) -> material.sprite(), new ModelResourceLocation(Barricade.asResource("advanced_barrier_item_renderer"), variant)),
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

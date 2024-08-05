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
import net.modgarden.barricade.client.renderer.block.DirectionalBarrierBlockRenderer;
import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.component.BlockedEntitiesComponent;
import net.modgarden.barricade.mixin.client.ModelBakeryAccessor;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntityBarrierItemRenderer {
    private static final Map<EntityBarrierComponents, BakedModel> MODEL_MAP = new HashMap<>();

    public static void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        EntityBarrierComponents components = new EntityBarrierComponents(stack.getOrDefault(BarricadeComponents.BLOCKED_ENTITIES, BlockedEntitiesComponent.EMPTY), stack.get(BarricadeComponents.BLOCKED_DIRECTIONS));
        BakedModel model = MODEL_MAP.computeIfAbsent(components, EntityBarrierItemRenderer::createModel);

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

    public static BakedModel createModel(EntityBarrierComponents components) {
        Map<String, Either<Material, String>> textureMap = new HashMap<>();
        textureMap.put("barricade_layer0", Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, components.blockedEntities().backTextureLocation())));

        if (components.blockedDirections() == null || !components.blockedDirections().blocksAll()) {
            textureMap.put("barricade_layer1", Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, DirectionalBarrierBlockRenderer.BARRIER_TEXTURE)));
        } else {
            textureMap.put("barricade_layer1", Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, DirectionalBarrierBlockRenderer.NO_BARRIER_TEXTURE)));
            int i = 2;
            for (Map.Entry<Direction, Boolean> entry : components.blockedDirections().directionMap().object2BooleanEntrySet()) {
                if (entry.getValue()) {
                    textureMap.put("barricade_layer" + i, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, DirectionalBarrierItemRenderer.DIRECTION_TO_MATERIAL_LOCATION.get(entry.getKey()))));
                    ++i;
                }
            }
        }
        String variant = components.blockedEntities().backTextureLocation() + "," + String.join(",", components.blockedEntities().entities().stream().map(tagKeyHolderEither -> tagKeyHolderEither.map(tag -> "#" + tag.location(), holder -> holder.unwrapKey().map(ResourceKey::location).orElse(ResourceLocation.withDefaultNamespace("null")).toString())).toList());
        if (components.blockedDirections() != null && !components.blockedDirections().doesNotBlock())
            variant = variant + "," + String.join(",", components.blockedDirections().directionMap().keySet().stream().map(Direction::getName).toList());

        BlockModel blockModel = new BlockModel(ResourceLocation.withDefaultNamespace("builtin/generated"), List.of(), textureMap, false, BlockModel.GuiLight.FRONT, Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(Items.BARRIER).getTransforms(), List.of());
        return ModelBakeryAccessor.getItemModelGenerator()
                .generateBlockModel(Material::sprite, blockModel)
                .bake(
                        BarricadeClient.getModelBakery().new ModelBakerImpl((modelLocation, material) -> material.sprite(), new ModelResourceLocation(Barricade.asResource("entity_barrier_block_renderer"), variant)),
                        blockModel,
                        Material::sprite,
                        BlockModelRotation.X0_Y0,
                        false
                );
    }

    public static void clearModelMap() {
        MODEL_MAP.clear();
    }

    public record EntityBarrierComponents(BlockedEntitiesComponent blockedEntities, @Nullable BlockedDirectionsComponent blockedDirections) {
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EntityBarrierComponents components))
                return false;
            if (components.blockedDirections == null && blockedDirections != null)
                return false;
            if (blockedDirections == null && components.blockedDirections != null)
                return false;
            return (components.blockedDirections == null || components.blockedDirections.equals(blockedDirections)) && components.blockedEntities.equals(blockedEntities);
        }

        @Override
        public int hashCode() {
            return Objects.hash(blockedEntities, blockedDirections);
        }
    }
}

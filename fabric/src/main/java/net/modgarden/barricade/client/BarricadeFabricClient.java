package net.modgarden.barricade.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.modgarden.barricade.client.renderer.block.DirectionalBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.block.EntityBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.item.DirectionalBarrierItemRenderer;
import net.modgarden.barricade.client.renderer.item.EntityBarrierItemRenderer;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeItems;

public class BarricadeFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(BarricadeBlockEntityTypes.DIRECTIONAL_BARRIER, context -> new DirectionalBarrierBlockRenderer());
        BlockEntityRendererRegistry.register(BarricadeBlockEntityTypes.ENTITY_BARRIER, context -> new EntityBarrierBlockRenderer());

        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.DIRECTIONAL_BARRIER, DirectionalBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.DOWN_BARRIER, DirectionalBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.UP_BARRIER, DirectionalBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.SOUTH_BARRIER, DirectionalBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.NORTH_BARRIER, DirectionalBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.EAST_BARRIER, DirectionalBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.WEST_BARRIER, DirectionalBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.HORIZONTAL_BARRIER, DirectionalBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.VERTICAL_BARRIER, DirectionalBarrierItemRenderer::renderItem);

        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.ENTITY_BARRIER, EntityBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.HOSTILE_BARRIER, EntityBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.MOB_BARRIER, EntityBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.PASSIVE_BARRIER, EntityBarrierItemRenderer::renderItem);
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.PLAYER_BARRIER, EntityBarrierItemRenderer::renderItem);
    }
}
package net.modgarden.barricade.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.modgarden.barricade.client.renderer.block.DirectionalBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.block.EntityBarrierBlockRenderer;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;

public class BarricadeFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(BarricadeBlockEntityTypes.DIRECTIONAL_BARRIER, context -> new DirectionalBarrierBlockRenderer());
        BlockEntityRendererRegistry.register(BarricadeBlockEntityTypes.ENTITY_BARRIER, context -> new EntityBarrierBlockRenderer());
    }
}

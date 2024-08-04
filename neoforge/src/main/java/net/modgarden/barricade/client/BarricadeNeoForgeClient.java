package net.modgarden.barricade.client;

import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.renderer.block.DirectionalBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.block.EntityBarrierBlockRenderer;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class BarricadeNeoForgeClient {
    @EventBusSubscriber(modid = Barricade.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerBERs(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(BarricadeBlockEntityTypes.DIRECTIONAL_BARRIER, context -> new DirectionalBarrierBlockRenderer());
            event.registerBlockEntityRenderer(BarricadeBlockEntityTypes.ENTITY_BARRIER, context -> new EntityBarrierBlockRenderer());
        }
    }
}

package net.modgarden.barricade.client;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.bewlr.BarricadeBEWLR;
import net.modgarden.barricade.client.renderer.block.DirectionalBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.block.EntityBarrierBlockRenderer;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class BarricadeNeoForgeClient {
    @EventBusSubscriber(modid = Barricade.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerBERs(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(BarricadeBlockEntityTypes.DIRECTIONAL_BARRIER, context -> new DirectionalBarrierBlockRenderer());
            event.registerBlockEntityRenderer(BarricadeBlockEntityTypes.ENTITY_BARRIER, context -> new EntityBarrierBlockRenderer());
        }

        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerItem(new IClientItemExtensions() {
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return BarricadeBEWLR.INSTANCE;
                }
            },
                    BarricadeItems.DIRECTIONAL_BARRIER,
                    BarricadeItems.DOWN_BARRIER,
                    BarricadeItems.UP_BARRIER,
                    BarricadeItems.SOUTH_BARRIER,
                    BarricadeItems.NORTH_BARRIER,
                    BarricadeItems.EAST_BARRIER,
                    BarricadeItems.WEST_BARRIER,
                    BarricadeItems.HORIZONTAL_BARRIER,
                    BarricadeItems.VERTICAL_BARRIER,
                    BarricadeItems.ENTITY_BARRIER,
                    BarricadeItems.HOSTILE_BARRIER,
                    BarricadeItems.MOB_BARRIER,
                    BarricadeItems.PASSIVE_BARRIER,
                    BarricadeItems.PLAYER_BARRIER
            );
        }
    }
}

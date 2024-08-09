package net.modgarden.barricade.neoforge.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.model.CreativeOnlyUnbakedModel;
import net.modgarden.barricade.neoforge.client.bewlr.BarricadeBEWLR;
import net.modgarden.barricade.client.renderer.block.AdvancedBarrierBlockRenderer;
import net.modgarden.barricade.neoforge.client.model.CreativeOnlyUnbakedModelGeometry;
import net.modgarden.barricade.neoforge.client.platform.BarricadeClientPlatformHelperNeoForge;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

@Mod(value = Barricade.MOD_ID, dist = Dist.CLIENT)
public class BarricadeNeoForgeClient {
    public BarricadeNeoForgeClient(IEventBus eventBus) {
        BarricadeClient.init(new BarricadeClientPlatformHelperNeoForge());
    }

    @EventBusSubscriber(modid = Barricade.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerBERs(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(BarricadeBlockEntityTypes.ADVANCED_BARRIER, context -> new AdvancedBarrierBlockRenderer());
        }

        @SubscribeEvent
        public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
            event.register(CreativeOnlyUnbakedModel.Deserializer.ID, new CreativeOnlyDeserializer());
        }

        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerItem(new IClientItemExtensions() {
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return BarricadeBEWLR.INSTANCE;
                }
            }, BarricadeItems.ADVANCED_BARRIER);
        }
    }

    public static class CreativeOnlyDeserializer implements IGeometryLoader<CreativeOnlyUnbakedModelGeometry> {
        private static final CreativeOnlyUnbakedModel.Deserializer DESERIALIZER = new CreativeOnlyUnbakedModel.Deserializer();

        @Override
        public CreativeOnlyUnbakedModelGeometry read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
            return new CreativeOnlyUnbakedModelGeometry(DESERIALIZER.deserialize(json, CreativeOnlyUnbakedModel.class, context));
        }
    }
}

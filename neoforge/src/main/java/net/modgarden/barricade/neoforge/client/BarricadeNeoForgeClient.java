package net.modgarden.barricade.neoforge.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.model.OperatorUnbakedModel;
import net.modgarden.barricade.client.particle.AdvancedBarrierParticle;
import net.modgarden.barricade.client.util.BarrierRenderUtils;
import net.modgarden.barricade.client.util.OperatorItemPseudoTag;
import net.modgarden.barricade.neoforge.client.bewlr.BarricadeBEWLR;
import net.modgarden.barricade.client.renderer.block.AdvancedBarrierBlockRenderer;
import net.modgarden.barricade.neoforge.client.model.CreativeOnlyUnbakedModelGeometry;
import net.modgarden.barricade.neoforge.client.platform.BarricadeClientPlatformHelperNeoForge;
import net.modgarden.barricade.particle.AdvancedBarrierParticleOptions;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

@Mod(value = Barricade.MOD_ID, dist = Dist.CLIENT)
public class BarricadeNeoForgeClient {
    public BarricadeNeoForgeClient(IEventBus eventBus) {
        BarricadeClient.init(new BarricadeClientPlatformHelperNeoForge());
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Barricade.MOD_ID, value = Dist.CLIENT)
    public static class GameEvents {
        private static boolean previousGameMasterBlockState = false;
        private static ItemStack lastItemInMainHand = ItemStack.EMPTY;
        private static ItemStack lastItemInOffHand = ItemStack.EMPTY;

        @SubscribeEvent
        public static void onRenderLevel(RenderLevelStageEvent event) {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null)
                    return;

                ItemStack mainHand = player.getInventory().items.get(player.getInventory().selected);
                ItemStack offHand = player.getInventory().offhand.getFirst();

                if (previousGameMasterBlockState != player.canUseGameMasterBlocks()) {
                    BarrierRenderUtils.refreshAllOperatorBlocks();
                    previousGameMasterBlockState = player.canUseGameMasterBlocks();
                }
                if (!ItemStack.isSameItemSameComponents(mainHand, lastItemInMainHand)) {
                    BarrierRenderUtils.refreshOperatorBlocks(mainHand, lastItemInMainHand, offHand);
                    lastItemInMainHand = mainHand.copy();
                }
                if (!ItemStack.isSameItemSameComponents(offHand, lastItemInOffHand)) {
                    BarrierRenderUtils.refreshOperatorBlocks(offHand, lastItemInOffHand, mainHand);
                    lastItemInOffHand = offHand.copy();
                }
            }
        }
    }

    @EventBusSubscriber(modid = Barricade.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerBERs(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(BarricadeBlockEntityTypes.ADVANCED_BARRIER, context -> new AdvancedBarrierBlockRenderer());
        }

        @SubscribeEvent
        public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
            event.register(OperatorUnbakedModel.Deserializer.ID, new CreativeOnlyDeserializer());
        }

        @SubscribeEvent
        public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(OperatorItemPseudoTag.Loader.INSTANCE);
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

        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpecial(AdvancedBarrierParticleOptions.Type.INSTANCE, new AdvancedBarrierParticle.Provider());
        }
    }

    public static class CreativeOnlyDeserializer implements IGeometryLoader<CreativeOnlyUnbakedModelGeometry> {
        private static final OperatorUnbakedModel.Deserializer DESERIALIZER = new OperatorUnbakedModel.Deserializer();

        @Override
        public CreativeOnlyUnbakedModelGeometry read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
            return new CreativeOnlyUnbakedModelGeometry(DESERIALIZER.deserialize(json, OperatorUnbakedModel.class, context));
        }
    }
}

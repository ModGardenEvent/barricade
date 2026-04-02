package net.modgarden.barricade.neoforge.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.command.BarricadeClientCommands;
import net.modgarden.barricade.client.model.OperatorUnbakedModel;
import net.modgarden.barricade.client.renderer.block.AdvancedBarrierBlockRenderer;
import net.modgarden.barricade.client.util.OperatorBlockPseudoTag;
import net.modgarden.barricade.neoforge.client.bewlr.BarricadeBEWLR;
import net.modgarden.barricade.neoforge.client.model.CreativeOnlyUnbakedModelGeometry;
import net.modgarden.barricade.neoforge.client.platform.BarricadeClientPlatformHelperNeoForge;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

@Mod(value = Barricade.MOD_ID, dist = Dist.CLIENT)
public class BarricadeNeoForgeClient {
	public BarricadeNeoForgeClient(IEventBus eventBus) {
		BarricadeClient.init(new BarricadeClientPlatformHelperNeoForge());
	}

	@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = Barricade.MOD_ID, value = Dist.CLIENT)
	public static class GameEvents {
		private static boolean previousInstabuildState = false;
		private static boolean previousAllVisibleState = false;
		private static Set<Either<Identifier, ResourceKey<Block>>> previousVisibleBlocks = Set.of();
		private static ItemStack lastItemInMainHand = ItemStack.EMPTY;
		private static ItemStack lastItemInOffHand = ItemStack.EMPTY;

		@SubscribeEvent
		public static void registerClientCommands(RegisterClientCommandsEvent event) {
			BarricadeClientCommands.registerClientCommands(event.getDispatcher(), event.getBuildContext());
		}

		@SubscribeEvent
		public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
			Barricade.serverContext = false;
		}

	@EventBusSubscriber(modid = Barricade.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ModEvents {
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public static void onClientSetup(FMLClientSetupEvent event) {
			File packCache = FMLPaths.GAMEDIR.get().resolve("barricade/enabled_resource_pack_cache").toFile();

			if (!packCache.exists()) {
				PackRepository repository = Minecraft.getInstance().getResourcePackRepository();
				repository.addPack("mod/" + Barricade.asResource("resourcepacks/modded_rendering"));
				try {
					Files.createDirectory(FMLPaths.GAMEDIR.get().resolve("barricade"));
					Files.createFile(packCache.toPath());
				} catch (IOException ex) {
					Barricade.LOG.error("Failed to create resource pack cache for Barricade.", ex);
				}
			}
		}

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
			event.registerReloadListener(OperatorBlockPseudoTag.Loader.INSTANCE);
		}

		@SubscribeEvent
		public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
			event.registerItem(new IClientItemExtensions() {
				@Override
				public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
					return BarricadeBEWLR.INSTANCE;
				}
			}, BarricadeItems.ADVANCED_BARRIER);
		}
	}

	public static class CreativeOnlyDeserializer implements IGeometryLoader<CreativeOnlyUnbakedModelGeometry> {
		private static final OperatorUnbakedModel.Deserializer DESERIALIZER = new OperatorUnbakedModel.Deserializer();

		@Override
		public @NotNull CreativeOnlyUnbakedModelGeometry read(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) throws JsonParseException {
			return new CreativeOnlyUnbakedModelGeometry(DESERIALIZER.deserialize(json, OperatorUnbakedModel.class, context));
		}
	}
}

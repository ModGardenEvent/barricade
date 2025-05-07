package net.modgarden.barricade.neoforge;

import house.greenhouse.greenhouseconfig.impl.SyncGreenhouseConfigTask;
import house.greenhouse.greenhouseconfig.impl.network.SyncGreenhouseConfigPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.*;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.network.clientbound.SetServerContextClientboundPacket;
import net.modgarden.barricade.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Mod(Barricade.MOD_ID)
public class BarricadeNeoForge {
	public BarricadeNeoForge(IEventBus eventBus) {
		Barricade.setHelper(new BarricadeNeoForgeHelper());
	}

	@EventBusSubscriber(modid = Barricade.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
	public static class GameEvents {
		@SubscribeEvent
		public static void registerContent(ServerStartingEvent event) {
			Barricade.serverContext = true;
		}
	}

	@EventBusSubscriber(modid = Barricade.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
	public static class ModEvents {
		@SubscribeEvent
		public static void registerContent(RegisterEvent event) {
			register(event, Registries.BLOCK, BarricadeBlocks::registerAll);
			register(event, Registries.BLOCK_ENTITY_TYPE, BarricadeBlockEntityTypes::registerAll);
			register(event, Registries.DATA_COMPONENT_TYPE, BarricadeComponents::registerAll);
			register(event, Registries.ITEM, BarricadeItems::registerAll);
			register(event, Registries.PARTICLE_TYPE, BarricadeParticleTypes::registerAll);
		}

		private static <T> void register(RegisterEvent event, ResourceKey<T> registryKey, Runnable registrar) {
			if (event.getRegistryKey() == registryKey)
				registrar.run();
		}

		@SubscribeEvent
		public static void newDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
			event.dataPackRegistry(BarricadeRegistries.ADVANCED_BARRIER, AdvancedBarrier.DIRECT_CODEC, AdvancedBarrier.DIRECT_CODEC);
		}

		@SubscribeEvent
		public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
				if (!event.hasPermissions() || !Barricade.serverContext)
					return;
				ItemStack startItem = null;
				for (ItemStack entry : event.getParentEntries()) {
					if (entry.is(Items.BARRIER)) {
						startItem = entry;
						break;
					}
				}
				if (startItem == null)
					return;

				List<Item> items = List.of(
						BarricadeItems.UP_BARRIER,
						BarricadeItems.DOWN_BARRIER,
						BarricadeItems.NORTH_BARRIER,
						BarricadeItems.SOUTH_BARRIER,
						BarricadeItems.WEST_BARRIER,
						BarricadeItems.EAST_BARRIER,
						BarricadeItems.HORIZONTAL_BARRIER,
						BarricadeItems.VERTICAL_BARRIER,
						BarricadeItems.PLAYER_BARRIER,
						BarricadeItems.MOB_BARRIER,
						BarricadeItems.PASSIVE_BARRIER,
						BarricadeItems.HOSTILE_BARRIER,
						BarricadeItems.CREATIVE_ONLY_BARRIER,
						BarricadeItems.CREATIVE_ONLY_LEVER
				);

				for (Item item : items) {
					ItemStack stack = new ItemStack(item);
					event.insertAfter(startItem, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					startItem = stack;
				}
			}
		}

		@SubscribeEvent
		public static void registerPayloads(RegisterPayloadHandlersEvent event) {
			event.registrar("1")
					.configurationToClient(SetServerContextClientboundPacket.TYPE, SetServerContextClientboundPacket.STREAM_CODEC, (packet, ctx) -> packet.handle());
		}

		@SubscribeEvent
		public static void registerConfigurationTasks(RegisterConfigurationTasksEvent event) {
			if (!event.getListener().hasChannel(SyncGreenhouseConfigPacket.TYPE) || event.getListener().getConnection().isMemoryConnection())
				return;
			event.register(new SyncGreenhouseConfigTask(event.getListener()));
		}

		@SubscribeEvent
		public static void addPackFinders(AddPackFindersEvent event) {
			if (event.getPackType() == PackType.CLIENT_RESOURCES)
				event.addPackFinders(Barricade.asResource("resourcepacks/modded_rendering"), PackType.CLIENT_RESOURCES, Component.translatable("resourcePack.barricade.modded_rendering.name"), createSource(true), false, Pack.Position.TOP);
		}

		private static PackSource createSource(boolean enabledByDefault) {
			return new PackSource() {
				@Override
				public @NotNull Component decorate(@NotNull Component component) {
					return Component.translatable("pack.barricade.builtin", component);
				}

				@Override
				public boolean shouldAddAutomatically() {
					return enabledByDefault;
				}
			};
		}
	}
}

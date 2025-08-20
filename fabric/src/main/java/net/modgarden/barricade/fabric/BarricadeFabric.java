package net.modgarden.barricade.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.network.clientbound.SetServerContextClientboundPacket;
import net.modgarden.barricade.registry.*;

public class BarricadeFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Barricade.setHelper(new BarricadeFabricHelper());
		BarricadeBlocks.registerAll();
		BarricadeBlockEntityTypes.registerAll();
		BarricadeComponents.registerAll();
		BarricadeItems.registerAll();
		BarricadeParticleTypes.registerAll();


		PayloadTypeRegistry.configurationS2C().register(SetServerContextClientboundPacket.TYPE, SetServerContextClientboundPacket.STREAM_CODEC);
		ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.register((handler, server) -> {
			if (!ServerConfigurationNetworking.canSend(handler, SetServerContextClientboundPacket.TYPE))
				return;
			ServerConfigurationNetworking.send(handler, new SetServerContextClientboundPacket());
		});
		ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> Barricade.serverContext = true);

		FabricLoader.getInstance().getModContainer(Barricade.MOD_ID).ifPresent(modContainer ->
			ResourceManagerHelper.registerBuiltinResourcePack(Barricade.asResource("modded_rendering"), modContainer, Component.translatable("resourcePack.barricade.modded_rendering.name"), ResourcePackActivationType.DEFAULT_ENABLED)
		);

		DynamicRegistries.registerSynced(BarricadeRegistries.ADVANCED_BARRIER, AdvancedBarrier.DIRECT_CODEC);

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register(entries -> {
			if (!entries.shouldShowOpRestrictedItems() || !Barricade.serverContext)
				return;
			entries.addAfter(Items.BARRIER,
					BarricadeItems.UP_BARRIER.get(),
					BarricadeItems.DOWN_BARRIER.get(),
					BarricadeItems.NORTH_BARRIER.get(),
					BarricadeItems.SOUTH_BARRIER.get(),
					BarricadeItems.WEST_BARRIER.get(),
					BarricadeItems.EAST_BARRIER.get(),
					BarricadeItems.HORIZONTAL_BARRIER.get(),
					BarricadeItems.VERTICAL_BARRIER.get(),
					BarricadeItems.PLAYER_BARRIER.get(),
					BarricadeItems.MOB_BARRIER.get(),
					BarricadeItems.PASSIVE_BARRIER.get(),
					BarricadeItems.HOSTILE_BARRIER.get(),
					BarricadeItems.CREATIVE_ONLY_BARRIER.get());
		});
	}
}

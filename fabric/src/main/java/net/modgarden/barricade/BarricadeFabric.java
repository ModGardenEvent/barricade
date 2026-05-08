package net.modgarden.barricade;

import static net.modgarden.barricade.BarricadeMod.id;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.modgarden.barricade.attachment.ModAttachments;
import net.modgarden.barricade.block.BarricadeBlock;
import net.modgarden.barricade.block.entity.BarricadeBlockEntity;
import net.modgarden.barricade.data.BarricadeData;
import net.modgarden.barricade.data.ClearableIdMapper;
import net.modgarden.barricade.network.clientbound.ClientboundSyncBarricadeDataPayload;
import net.modgarden.barricade.network.clientbound.ClientboundSyncDynamicRegistriesPayload;
import net.modgarden.barricade.network.clientbound.SetServerContextClientboundPacket;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeComponents;
import net.modgarden.barricade.registry.BarricadeItems;
import net.modgarden.barricade.registry.BarricadeParticleTypes;
import net.modgarden.barricade.registry.BarricadeRegistries;
import net.modgarden.barricade.registry.BarricadeValueTypes;

public class BarricadeFabric implements ModInitializer {
	public static final Set<BarricadeBlockEntity> BARRICADES_TO_PROCESS = new HashSet<>();

	@Override
	public void onInitialize() {
		BarricadeMod.setHelper(new BarricadeFabricHelper());
		BarricadeBlocks.registerAll();
		BarricadeComponents.registerAll();
		BarricadeItems.registerAll();
		BarricadeParticleTypes.registerAll();
		BarricadeBlockEntityTypes.initialize();

		ServerTickEvents.END_SERVER_TICK.register(_ -> {
			for (BarricadeBlockEntity blockEntity : BARRICADES_TO_PROCESS) {
				if (!blockEntity.isRemoved() && blockEntity.id != -1) {
					Holder<BarricadeData> holder = BarricadeData.ID_MAPPER.byId(blockEntity.id);

					if (holder == null || !holder.isBound()) {
						holder = BarricadeData.UNKNOWN_HOLDER;
					}

					BarricadeBlock.setBarricadeData(Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos(), holder);
				}
			}

			BARRICADES_TO_PROCESS.clear();
		});

		// what the fuck is this
		PayloadTypeRegistry.clientboundConfiguration().register(SetServerContextClientboundPacket.TYPE, SetServerContextClientboundPacket.STREAM_CODEC);
		ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.register((handler, _) -> {
			if (!ServerConfigurationNetworking.canSend(handler, SetServerContextClientboundPacket.TYPE))
				return;
			ServerConfigurationNetworking.send(handler, new SetServerContextClientboundPacket());
		});
		ServerLifecycleEvents.SERVER_STARTING.register(_ -> BarricadeMod.serverContext = true);

		PayloadTypeRegistry.clientboundPlay().register(ClientboundSyncDynamicRegistriesPayload.TYPE, ClientboundSyncDynamicRegistriesPayload.STREAM_CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ClientboundSyncBarricadeDataPayload.TYPE, ClientboundSyncBarricadeDataPayload.STREAM_CODEC);

		FabricLoader.getInstance().getModContainer(BarricadeMod.MOD_ID).ifPresent(modContainer ->
			ResourceManagerHelper.registerBuiltinResourcePack(id("modded_rendering"), modContainer, Component.translatable("resourcePack.barricade.modded_rendering.name"), ResourcePackActivationType.DEFAULT_ENABLED)
		);

		BarricadeValueTypes.initialize();

		DynamicRegistries.registerSynced(BarricadeRegistries.BARRICADE, BarricadeData.DIRECT_CODEC);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, _) -> {
			Registry<BarricadeData> registry = Objects.requireNonNull(
					player.level(),
					"Must be in a level to synchronize dynamic registries"
			).registryAccess().lookupOrThrow(BarricadeRegistries.BARRICADE);
			// TODO: cache this?
			Int2ObjectMap<Holder<BarricadeData>> map = new Int2ObjectOpenHashMap<>();
			IdMap<Holder<BarricadeData>> holderIdMap = registry.asHolderIdMap();

			for (Holder<BarricadeData> holder : holderIdMap) {
				map.put(holderIdMap.getId(holder), holder);
			}

			ServerPlayNetworking.send(player, new ClientboundSyncDynamicRegistriesPayload(map));
		});
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, _, success) -> {
			if (success) {
				prepareIdMapper(server);
			}
		});
		ServerLifecycleEvents.SERVER_STARTED.register(BarricadeFabric::prepareIdMapper);

		ModAttachments.initialize();

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.OP_BLOCKS).register(entries -> {
			if (!entries.shouldShowOpRestrictedItems() || !BarricadeMod.serverContext)
				return;
			ClientLevel level = Minecraft.getInstance().level;

			if (level == null) return;

			for (Holder<BarricadeData> holder : BarricadeData.ID_MAPPER) {
				ItemStack stack = new ItemStack(BarricadeItems.BARRICADE.get());
				stack.set(BarricadeComponents.BARRICADE, holder);
				entries.insertAfter(Items.BARRIER, stack);
			}
		});
	}

	private static void prepareIdMapper(MinecraftServer server) {
		// TODO: force synchronization of all BarricadePalette data attachments if appropriate
		((ClearableIdMapper) BarricadeData.ID_MAPPER).barricade$clear();
		Registry<BarricadeData> registry = server.registryAccess().lookupOrThrow(BarricadeRegistries.BARRICADE);
		IdMap<Holder<BarricadeData>> holderIdMap = registry.asHolderIdMap();

		for (Holder<BarricadeData> holder : holderIdMap) {
			BarricadeData.ID_MAPPER.addMapping(holder, holderIdMap.getId(holder));
		}

		Holder<BarricadeData> defaultHolder = registry.getOrThrow(ResourceKey.create(
				BarricadeRegistries.BARRICADE,
				id("unknown")
		));
		BarricadeData.ID_MAPPER.addMapping(
				BarricadeData.UNKNOWN_HOLDER,
				holderIdMap.getId(defaultHolder)
		);
	}
}

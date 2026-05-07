package net.modgarden.barricade.client;

import static net.modgarden.barricade.BarricadeMod.id;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.client.command.BarricadeClientCommands;
import net.modgarden.barricade.client.model.BarricadeBlockStateModel;
import net.modgarden.barricade.client.renderer.BarricadeRendering;
import net.modgarden.barricade.client.util.OperatorBlockPseudoTag;
import net.modgarden.barricade.client.platform.BarricadeClientPlatformHelperFabric;
import net.modgarden.barricade.data.BarricadeData;
import net.modgarden.barricade.data.ClearableIdMapper;
import net.modgarden.barricade.network.clientbound.ClientboundSyncDynamicRegistriesPayload;
import net.modgarden.barricade.network.clientbound.SetServerContextClientboundPacket;
import net.modgarden.barricade.registry.BarricadeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BarricadeFabricClient implements ClientModInitializer {
	public static final Map<Identifier, BarricadeBlockStateModel> MODELS = new HashMap<>();

	@Override
	public void onInitializeClient() {
		BarricadeClient.init(new BarricadeClientPlatformHelperFabric());

		// what???
		ClientConfigurationNetworking.registerGlobalReceiver(SetServerContextClientboundPacket.TYPE, (packet, _) -> packet.handle());
		ClientLoginConnectionEvents.DISCONNECT.register((_, _) -> BarricadeMod.serverContext = false);

		ClientCommandRegistrationCallback.EVENT.register(BarricadeClientCommands::registerClientCommands);

		ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncDynamicRegistriesPayload.TYPE, (payload, context) -> {
			((ClearableIdMapper) BarricadeData.ID_MAPPER).barricade$clear();

			for (Int2ObjectMap.Entry<Holder<BarricadeData>> entry : payload.holderIdMap().int2ObjectEntrySet()) {
				int id = entry.getIntKey();
				Holder<BarricadeData> holder = entry.getValue();
				BarricadeData.ID_MAPPER.addMapping(holder, id);
			}

			Holder<BarricadeData> defaultHolder = Objects.requireNonNull(context.client().level)
					.registryAccess()
					.getOrThrow(ResourceKey.create(
							BarricadeRegistries.BARRICADE,
							id("default")
					));
			BarricadeData.ID_MAPPER.addMapping(BarricadeData.DEFAULT_HOLDER, BarricadeData.ID_MAPPER.getId(defaultHolder));
		});

		BarricadeRendering.initialize();

		// what the fuck is this
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
			private final OperatorBlockPseudoTag.Loader listener = OperatorBlockPseudoTag.Loader.INSTANCE;

			@Override
			public CompletableFuture<Void> reload(
					SharedState currentReload,
					Executor taskExecutor,
					PreparationBarrier preparationBarrier,
					Executor reloadExecutor
			) {
				return listener.reload(currentReload, taskExecutor, preparationBarrier, reloadExecutor);
			}

			@Override
			public Identifier getFabricId() {
				return id("operator_blocks");
			}
		});
	}
}

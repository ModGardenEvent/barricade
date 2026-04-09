package net.modgarden.barricade.client;

import static net.modgarden.barricade.BarricadeMod.id;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.client.command.BarricadeClientCommands;
import net.modgarden.barricade.client.model.BarricadeBlockStateModel;
import net.modgarden.barricade.client.renderer.block.AdvancedBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.block.BakedRegion;
import net.modgarden.barricade.client.util.OperatorBlockPseudoTag;
import net.modgarden.barricade.client.platform.BarricadeClientPlatformHelperFabric;
import net.modgarden.barricade.network.clientbound.SetServerContextClientboundPacket;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;

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
		BlockEntityRenderers.register(BarricadeBlockEntityTypes.ADVANCED_BARRIER, AdvancedBarrierBlockRenderer::new);

		ClientConfigurationNetworking.registerGlobalReceiver(SetServerContextClientboundPacket.TYPE, (packet, _) -> packet.handle());
		ClientLoginConnectionEvents.DISCONNECT.register((_, _) -> BarricadeMod.serverContext = false);

		ClientCommandRegistrationCallback.EVENT.register(BarricadeClientCommands::registerClientCommands);

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

		LevelRenderEvents.END_EXTRACTION.register(context -> BakedRegion.extract(new BakedRegion.ExtractContext(
				context.level(),
				Objects.requireNonNull(Minecraft.getInstance().player, "What does non-existence feel like, I wonder?"),
				context.deltaTracker().getGameTimeDeltaTicks(),
				context.levelState()
		)));
		LevelRenderEvents.START_MAIN.register(_ -> BakedRegion.bakeDirty());
		LevelRenderEvents.AFTER_OPAQUE_TERRAIN.register(_ -> BakedRegion.renderRegions());
		LevelRenderEvents.END_MAIN.register(_ -> {
			BakedRegion.uploadDirty();
			BakedRegion.onRenderEnd();
		});

		ClientChunkEvents.CHUNK_LOAD.register((level, chunk)-> {
			//noinspection ConstantValue
			assert BakedRegion.SIZE_XYZ == 16; // just in case someone changes it
			for (int i = level.getMinSectionY(); i <= level.getMaxSectionY(); i++) {
				BakedRegion.putRegion(new BakedRegion.BakedRegionPos(chunk.getPos().x(), i, chunk.getPos().z()));
			}
		});
		ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk)-> {
			//noinspection ConstantValue
			assert BakedRegion.SIZE_XYZ == 16; // just in case someone changes it
			for (int i = level.getMinSectionY(); i <= level.getMaxSectionY(); i++) {
				BakedRegion.removeRegion(new BakedRegion.BakedRegionPos(chunk.getPos().x(), i, chunk.getPos().z()));
			}
		});
		ClientPlayConnectionEvents.DISCONNECT.register((_, _) -> BakedRegion.clearRegions());
	}
}

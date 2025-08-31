package net.modgarden.barricade.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Blocks;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.command.BarricadeClientCommands;
import net.modgarden.barricade.client.renderer.block.AdvancedBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.block.BakedRegion;
import net.modgarden.barricade.client.util.OperatorBlockPseudoTag;
import net.modgarden.barricade.fabric.client.platform.BarricadeClientPlatformHelperFabric;
import net.modgarden.barricade.network.clientbound.SetServerContextClientboundPacket;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BarricadeFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BarricadeClient.init(new BarricadeClientPlatformHelperFabric());
		BlockEntityRenderers.register(BarricadeBlockEntityTypes.ADVANCED_BARRIER, AdvancedBarrierBlockRenderer::new);

		ClientConfigurationNetworking.registerGlobalReceiver(SetServerContextClientboundPacket.TYPE, (packet, ctx) -> packet.handle());
		ClientLoginConnectionEvents.DISCONNECT.register((listener, minecraft) -> Barricade.serverContext = false);

		ClientCommandRegistrationCallback.EVENT.register(BarricadeClientCommands::registerClientCommands);

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
			private final OperatorBlockPseudoTag.Loader listener = OperatorBlockPseudoTag.Loader.INSTANCE;

			@Override
			public @NotNull CompletableFuture<Void> reload(
					PreparationBarrier barrier,
					ResourceManager manager,
					Executor backgroundExecutor,
					Executor gameExecutor
			) {
				return listener.reload(barrier, manager, backgroundExecutor, gameExecutor);
			}

			@Override
			public ResourceLocation getFabricId() {
				return Barricade.asResource("operator_blocks");
			}
		});

		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
				Blocks.BARRIER,
				Blocks.LIGHT,
				BarricadeBlocks.ADVANCED_BARRIER.get(),
				BarricadeBlocks.UP_BARRIER.get(),
				BarricadeBlocks.DOWN_BARRIER.get(),
				BarricadeBlocks.NORTH_BARRIER.get(),
				BarricadeBlocks.SOUTH_BARRIER.get(),
				BarricadeBlocks.WEST_BARRIER.get(),
				BarricadeBlocks.EAST_BARRIER.get(),
				BarricadeBlocks.HORIZONTAL_BARRIER.get(),
				BarricadeBlocks.VERTICAL_BARRIER.get(),
				BarricadeBlocks.PLAYER_BARRIER.get(),
				BarricadeBlocks.MOB_BARRIER.get(),
				BarricadeBlocks.PASSIVE_BARRIER.get(),
				BarricadeBlocks.HOSTILE_BARRIER.get(),
				BarricadeBlocks.CREATIVE_ONLY_BARRIER.get()
		);

		ClientPlayerBlockBreakEvents.AFTER.register((level, player, pos, state) -> {
			if (state.is(BarricadeTags.BlockTags.BARRIERS)) {
				BakedRegion.putRegion(BakedRegion.BakedRegionPos.fromBlockPos(pos));
			}
		});

		WorldRenderEvents.START.register(context -> {
			PoseStack poseStack = new PoseStack();
			poseStack.pushPose();
			BakedRegion.buildDirty(new BakedRegion.UploadContext(context.world(), poseStack));
			poseStack.popPose();
		});

		WorldRenderEvents.AFTER_ENTITIES.register(context -> BakedRegion.renderRegions(new BakedRegion.RenderContext()));
	}
}

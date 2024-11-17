package net.modgarden.barricade.fabric.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.model.OperatorUnbakedModel;
import net.modgarden.barricade.client.util.BarrierRenderUtils;
import net.modgarden.barricade.client.util.OperatorItemPseudoTag;
import net.modgarden.barricade.fabric.client.platform.BarricadeClientPlatformHelperFabric;
import net.modgarden.barricade.client.renderer.block.AdvancedBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.item.AdvancedBarrierItemRenderer;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeItems;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class BarricadeFabricClient implements ClientModInitializer {
    private static boolean previousGameMasterBlockState = false;
    private static ItemStack lastItemInMainHand = ItemStack.EMPTY;
    private static ItemStack lastItemInOffHand = ItemStack.EMPTY;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(OperatorUnbakedModel.class, new OperatorUnbakedModel.Deserializer())
            .create();

    @Override
    public void onInitializeClient() {
        BarricadeClient.init(new BarricadeClientPlatformHelperFabric());
        BlockEntityRenderers.register(BarricadeBlockEntityTypes.ADVANCED_BARRIER, context -> new AdvancedBarrierBlockRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(BarricadeItems.ADVANCED_BARRIER, AdvancedBarrierItemRenderer::renderItem);

        PreparableModelLoadingPlugin.register(BarricadeFabricClient::getCreativeUnbakedModels, (data, pluginContext) -> {
            pluginContext.resolveModel().register((context) -> {
                if (data.containsKey(context.id()))
                    return data.get(context.id());
                return null;
            });
        });

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            private final OperatorItemPseudoTag.Loader listener = OperatorItemPseudoTag.Loader.INSTANCE;

            @Override
            public ResourceLocation getFabricId() {
                return Barricade.asResource("operator_items");
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return listener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        });

        WorldRenderEvents.END.register(context -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null)
                return;
            if (previousGameMasterBlockState != player.canUseGameMasterBlocks()) {
                BarrierRenderUtils.refreshAllOperatorBlocks();
                previousGameMasterBlockState = player.canUseGameMasterBlocks();
                return;
            }
            if (!ItemStack.isSameItemSameComponents(player.getMainHandItem(), lastItemInMainHand)) {
                BarrierRenderUtils.refreshOperatorBlocks(player.getMainHandItem(), lastItemInMainHand);
                lastItemInMainHand = player.getMainHandItem();
            }
            if (!ItemStack.isSameItemSameComponents(player.getOffhandItem(), lastItemInOffHand)) {
                BarrierRenderUtils.refreshOperatorBlocks(player.getOffhandItem(), lastItemInOffHand);
                lastItemInOffHand = player.getOffhandItem();
            }
        });

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                Blocks.BARRIER,
                Blocks.LIGHT,
                BarricadeBlocks.ADVANCED_BARRIER,
                BarricadeBlocks.UP_BARRIER,
                BarricadeBlocks.DOWN_BARRIER,
                BarricadeBlocks.NORTH_BARRIER,
                BarricadeBlocks.SOUTH_BARRIER,
                BarricadeBlocks.WEST_BARRIER,
                BarricadeBlocks.EAST_BARRIER,
                BarricadeBlocks.HORIZONTAL_BARRIER,
                BarricadeBlocks.VERTICAL_BARRIER,
                BarricadeBlocks.PLAYER_BARRIER,
                BarricadeBlocks.MOB_BARRIER,
                BarricadeBlocks.PASSIVE_BARRIER,
                BarricadeBlocks.HOSTILE_BARRIER
        );
    }

    private static CompletableFuture<Map<ResourceLocation, OperatorUnbakedModel>> getCreativeUnbakedModels(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> manager.listResources("models", fileName -> fileName.getPath().endsWith(".json")), executor).thenCompose(models -> {
            ArrayList<CompletableFuture<Pair<ResourceLocation, OperatorUnbakedModel>>> creativeModels = new ArrayList<>();
            for (Map.Entry<ResourceLocation, Resource> resource : models.entrySet()) {
                creativeModels.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        ResourceLocation resourceLocation = resource.getKey().withPath(s -> s.substring(7, s.length() - 5));
                        Reader reader = resource.getValue().openAsReader();
                        JsonElement element = JsonParser.parseReader(reader);
                        reader.close();
                        if (!element.isJsonObject() || !element.getAsJsonObject().has("loader"))
                            return null;
                        String loaderKey = GsonHelper.getAsString(element.getAsJsonObject(), "loader");
                        if (!loaderKey.equals(OperatorUnbakedModel.Deserializer.ID.toString()))
                            return null;

                        return Pair.of(resourceLocation, GSON.fromJson(element, OperatorUnbakedModel.class));
                    } catch (Exception ex) {
                        Barricade.LOG.error("Failed to load 'barricade:creative_mode' model", ex);
                    }
                    return null;
                }, executor));
            }
            return Util.sequenceFailFast(creativeModels).thenApply(pairs -> pairs.stream().filter(Objects::nonNull).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        });
    }
}
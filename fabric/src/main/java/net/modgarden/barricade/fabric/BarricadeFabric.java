package net.modgarden.barricade.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeComponents;
import net.modgarden.barricade.registry.BarricadeItems;
import net.modgarden.barricade.registry.BarricadeRegistries;

public class BarricadeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BarricadeBlocks.registerAll(Registry::register);
        BarricadeBlockEntityTypes.registerAll(Registry::register);
        BarricadeComponents.registerAll(Registry::register);
        BarricadeItems.registerAll(Registry::register);

        FabricLoader.getInstance().getModContainer(Barricade.MOD_ID).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(Barricade.asResource("modded_rendering"), modContainer, Component.translatable("resourcePack.barricade.modded_rendering.name"), ResourcePackActivationType.NORMAL);
        });
        DynamicRegistries.registerSynced(BarricadeRegistries.ADVANCED_BARRIER, AdvancedBarrier.DIRECT_CODEC);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.OP_BLOCKS).register(entries -> {
            if (!entries.shouldShowOpRestrictedItems())
                return;
            entries.addAfter(Items.BARRIER,
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
                    BarricadeItems.CREATIVE_ONLY_BARRIER);
        });
    }
}

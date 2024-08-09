package net.modgarden.barricade.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeComponents;
import net.modgarden.barricade.registry.BarricadeItems;

public class BarricadeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BarricadeBlocks.registerAll(Registry::register);
        BarricadeBlockEntityTypes.registerAll(Registry::register);
        BarricadeComponents.registerAll(Registry::register);
        BarricadeItems.registerAll(Registry::register);

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
                    BarricadeItems.HOSTILE_BARRIER);
        });
    }
}

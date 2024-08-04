package net.modgarden.barricade;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeComponents;
import net.modgarden.barricade.registry.BarricadeItems;

public class BarricadeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Barricade.init();
        BarricadeBlocks.registerAll(Registry::register);
        BarricadeBlockEntityTypes.registerAll(Registry::register);
        BarricadeComponents.registerAll(Registry::register);
        BarricadeItems.registerAll(Registry::register);
    }
}

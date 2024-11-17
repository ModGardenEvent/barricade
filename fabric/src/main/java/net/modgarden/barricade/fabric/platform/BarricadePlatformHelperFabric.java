package net.modgarden.barricade.fabric.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.modgarden.barricade.platform.BarricadePlatformHelper;

public class BarricadePlatformHelperFabric implements BarricadePlatformHelper {
    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}

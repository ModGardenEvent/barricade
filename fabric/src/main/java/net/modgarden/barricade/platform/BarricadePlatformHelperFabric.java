package net.modgarden.barricade.platform;

import net.fabricmc.loader.api.FabricLoader;

public class BarricadePlatformHelperFabric implements BarricadePlatformHelper {

    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
package net.modgarden.barricade.platform;

import net.fabricmc.loader.api.FabricLoader;

import java.util.Collection;

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

    @Override
    public <T> Collection<T> fixSeamsOnNeoForge(Collection<T> collection, Object textureAtlasSprite) {
        return collection;
    }
}

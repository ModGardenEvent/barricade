package net.modgarden.barricade.platform;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class BarricadePlatformHelperNeoForge implements BarricadePlatformHelper {

    @Override
    public Platform getPlatform() {

        return Platform.NEOFORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }
}
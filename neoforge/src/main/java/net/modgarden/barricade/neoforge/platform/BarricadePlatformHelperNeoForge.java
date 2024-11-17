package net.modgarden.barricade.neoforge.platform;

import net.modgarden.barricade.platform.BarricadePlatformHelper;
import net.neoforged.fml.ModList;

public class BarricadePlatformHelperNeoForge implements BarricadePlatformHelper {
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}

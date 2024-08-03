package net.modgarden.barricade;

import net.modgarden.barricade.platform.BarricadePlatformHelperFabric;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class BarricadeFabricPre implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        Barricade.setHelper(new BarricadePlatformHelperFabric());
    }
}

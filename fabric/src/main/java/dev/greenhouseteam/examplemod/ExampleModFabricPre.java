package dev.greenhouseteam.examplemod;

import dev.greenhouseteam.examplemod.platform.ExamplePlatformHelperFabric;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class ExampleModFabricPre implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        ExampleMod.setHelper(new ExamplePlatformHelperFabric());
    }
}

package net.modgarden.barricade;

import net.minecraft.resources.ResourceLocation;
import net.modgarden.barricade.platform.BarricadePlatformHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Barricade {
    public static final String MOD_ID = "barricade";
    public static final String MOD_NAME = "Barricade";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private static BarricadePlatformHelper helper;

    public static void init(BarricadePlatformHelper helper) {
        if (Barricade.helper != null)
            return;
        Barricade.helper = helper;
    }

    public static BarricadePlatformHelper getHelper() {
        return helper;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
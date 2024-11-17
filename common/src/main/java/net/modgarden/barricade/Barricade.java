package net.modgarden.barricade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Barricade {
    public static final String MOD_ID = "barricade";
    public static final String MOD_NAME = "Barricade";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    // The below is only added to on the client, but we don't want to call to it.
    private static final List<ResourceLocation> operatorModels = new ArrayList<>();

    public static boolean isOperatorModel(Block block) {
        return operatorModels.contains(block.builtInRegistryHolder().key().location());
    }

    public static void addToOperatorModelCache(ResourceLocation id) {
        operatorModels.add(id.withPath(s -> {
            String split = s.split("/", 3)[2];
            return split.substring(0, split.length() - 5);
        }));
    }

    public static void removeFromOperatorModelCache(ResourceLocation id) {
        operatorModels.remove(id.withPath(s -> {
            String split = s.split("/", 3)[2];
            return split.substring(0, split.length() - 5);
        }));
    }
    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
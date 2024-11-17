package net.modgarden.barricade;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.model.OperatorBakedModelAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Barricade {
    public static final String MOD_ID = "barricade";
    public static final String MOD_NAME = "Barricade";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static boolean isOperatorModel(BlockState state) {
        return BarricadeClient.getHelper() != null && Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
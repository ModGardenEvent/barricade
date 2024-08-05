package net.modgarden.barricade.client;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.model.ModelBakery;

import java.util.List;

public class BarricadeClient {
    public static final List<String> BARRICADE_LAYERS = Lists.newArrayList("barricade_layer0", "barricade_layer1", "barricade_layer2", "barricade_layer3", "barricade_layer4", "barricade_layer5", "barricade_layer6", "barricade_layer7");
    public static ModelBakery modelBakery;

    public static ModelBakery getModelBakery() {
        return modelBakery;
    }

    public static void setModelBakery(ModelBakery modelBakery) {
        BarricadeClient.modelBakery = modelBakery;
    }
}

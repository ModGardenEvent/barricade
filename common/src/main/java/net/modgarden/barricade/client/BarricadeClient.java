package net.modgarden.barricade.client;

import com.google.common.collect.Lists;
import house.greenhouse.greenhouseconfig.api.GreenhouseConfigHolder;
import house.greenhouse.greenhouseconfig.jsonc.JsonCLang;
import net.minecraft.client.resources.model.ModelBakery;
import net.modgarden.barricade.client.platform.BarricadeClientPlatformHelper;
import net.modgarden.barricade.client.renderer.block.BakedBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.block.BakedRegion;

import java.util.List;

public class BarricadeClient {
	public static final List<String> BARRICADE_LAYERS = Lists.newArrayList("barricade_layer0", "barricade_layer1", "barricade_layer2", "barricade_layer3", "barricade_layer4", "barricade_layer5", "barricade_layer6", "barricade_layer7");
	public static ModelBakery modelBakery;
	public static BarricadeClientPlatformHelper helper;

	public static final GreenhouseConfigHolder<BarricadeClientConfig> CONFIG = GreenhouseConfigHolder.client("barricade", BarricadeClientConfig.CODEC, BarricadeClientConfig.DEFAULT, JsonCLang.INSTANCE)
			.schemaVersion(2)
			.dataFixer(BarricadeClientConfig.Fixer.INSTANCE)
			.build();

	public static void init(BarricadeClientPlatformHelper helper) {
		if (BarricadeClient.helper != null)
			return;

		BarricadeClient.helper = helper;

		BakedRegion.registerRegionBaker(BakedBarrierBlockRenderer::new);
	}

	public static BarricadeClientPlatformHelper getHelper() {
		return helper;
	}

	public static ModelBakery getModelBakery() {
		return modelBakery;
	}

	public static void setModelBakery(ModelBakery modelBakery) {
		BarricadeClient.modelBakery = modelBakery;
	}
}

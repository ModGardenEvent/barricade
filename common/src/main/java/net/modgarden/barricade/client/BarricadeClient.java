package net.modgarden.barricade.client;

import lgbt.greenhouse.config.api.v3.GreenhouseConfigHolder;
import net.modgarden.barricade.client.platform.BarricadeClientPlatformHelper;
import net.modgarden.barricade.client.renderer.block.BakedBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.block.BakedRegion;

public class BarricadeClient {
	public static BarricadeClientPlatformHelper helper;

	public static final GreenhouseConfigHolder<BarricadeClientConfig> CONFIG = GreenhouseConfigHolder.client("barricade", BarricadeClientConfig.CODEC, BarricadeClientConfig.DEFAULT, JsonCLang.INSTANCE)
			.schemaVersion(3)
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
}

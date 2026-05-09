package net.modgarden.barricade.client;

import java.util.List;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lgbt.greenhouse.config.api.v3.GreenhouseConfigHolder;
import lgbt.greenhouse.config.api.v3.GreenhouseConfigSide;
import lgbt.greenhouse.config.api.v3.builder.DefaultValueCommentSettings;
import lgbt.greenhouse.config.api.v3.lang.GreenhouseConfigJsonCLang;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.client.platform.BarricadeClientPlatformHelper;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class BarricadeClient {
	public static BarricadeClientPlatformHelper helper;

	public static final GreenhouseConfigHolder<BarricadeClientConfig> CONFIG = GreenhouseConfigHolder.register(
			BarricadeClientConfig.class,
			BarricadeMod.MOD_ID,
			4,
			GreenhouseConfigJsonCLang.INSTANCE,
			GreenhouseConfigSide.CLIENT,
			builder -> builder
					.withValue(
							"everything_visible",
							"""
							If all invisible blocks are visible to you, not accounting the item you are holding.
							The player must be in creative to see barriers.
							""",
							Codec.BOOL,
							BarricadeClientConfig.DEFAULT.everythingVisible(),
							BarricadeClientConfig::everythingVisible,
							DefaultValueCommentSettings.PREPEND
					)
					.withValue(
							"disable_in_survival",
							"""
							Whether invisible blocks should be invisible in survival.
							WARNING: This will cause flickering when changing game modes!
							""",
							Codec.BOOL,
							BarricadeClientConfig.DEFAULT.disableInSurvival(),
							BarricadeClientConfig::disableInSurvival,
							DefaultValueCommentSettings.PREPEND
					)
					.withValue(
							"visible_blocks",
							"""
							Which operator blocks are visible to you, not accounting the item you are holding.
							Accepts a list of mixed block ids or operator blocks tags found within assets/<namespace>/barricade/operator_blocks/<path>.json
							The player must be in creative to see barriers.
							""",
							Codec.either(Codec.STRING.comapFlatMap(s -> {
								if (s.startsWith("#"))
									return DataResult.success(Identifier.tryParse(s.substring(1)));
								return DataResult.error(() -> "Not an operator block tag");
							}, rl -> "#" + rl.toString()), ResourceKey.codec(Registries.BLOCK)).listOf().xmap(Set::copyOf, List::copyOf),
							BarricadeClientConfig.DEFAULT.visibleBlocks(),
							BarricadeClientConfig::visibleBlocks
					)
					.withValue(
							"barrier_fade_time",
							"""
							How long (in seconds) barriers should take to fade when a barrier is equipped or unequipped.
							Set this to 0 to disable fading.
							""",
							Codec.FLOAT,
							BarricadeClientConfig.DEFAULT.barrierFadeTime(),
							BarricadeClientConfig::barrierFadeTime
					)
	);

	public static void init(BarricadeClientPlatformHelper helper) {
		if (BarricadeClient.helper != null)
			return;

		BarricadeClient.helper = helper;
	}

	public static BarricadeClientPlatformHelper getHelper() {
		return helper;
	}
}

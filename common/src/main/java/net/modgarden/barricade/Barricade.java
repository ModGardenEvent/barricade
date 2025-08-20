package net.modgarden.barricade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.platform.BarricadePlatformHelper;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Barricade {
	public static final String MOD_ID = "barricade";
	public static final String MOD_NAME = "Barricade";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
	private static BarricadePlatformHelper helper;

	public static boolean serverContext;

	public static boolean isOperatorModel(BlockState state) {
		if (state.getBlock() == BarricadeBlocks.ADVANCED_BARRIER)
			state = Blocks.BARRIER.defaultBlockState();
		return state.is(BarricadeTags.BlockTags.BARRIERS);
	}

	public static ResourceLocation asResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public static BarricadePlatformHelper getHelper() {
		return helper;
	}

	public static void setHelper(BarricadePlatformHelper helper) {
		Barricade.helper = helper;
	}
}

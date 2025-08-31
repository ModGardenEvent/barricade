package net.modgarden.barricade.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.client.renderer.block.BakedRegion;
import net.modgarden.barricade.data.BlockedDirections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class StaticBarrierBlock extends BarrierBlock {
	public static final Map<ResourceLocation, StaticBarrierBlock> BARRIERS = new HashMap<>();

	protected StaticBarrierBlock(Properties properties) {
		super(properties);
	}

	public abstract BlockedDirections getBlockedDirections();

	public abstract @Nullable ResourceLocation getIcon();

	@Override
	protected void onPlace(
			@NotNull BlockState state,
			@NotNull Level level,
			@NotNull BlockPos pos,
			@NotNull BlockState oldState,
			boolean movedByPiston
	) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		BakedRegion.putRegion(BakedRegion.BakedRegionPos.fromBlockPos(pos));
	}
}

package net.modgarden.barricade.mixin.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.client.renderer.block.BakedRegion;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BarrierBlock.class)
public abstract class BarrierBlockMixin extends Block {
	private BarrierBlockMixin(Properties p_49795_) {
		super(p_49795_);
	}

	@Override
	protected void onPlace(
			@NotNull BlockState state,
			@NotNull Level level,
			@NotNull BlockPos pos,
			@NotNull BlockState oldState,
			boolean movedByPiston
	) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		BakedRegion.markRegionDirty(BakedRegion.BakedRegionPos.fromBlockPos(pos));
	}
}

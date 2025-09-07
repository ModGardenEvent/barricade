package net.modgarden.barricade.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.registry.BarricadeTags;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BarrierBlock.class)
public final class BarrierBlockMixin extends Block {
	public BarrierBlockMixin(Properties p_49795_) {
		super(p_49795_);
	}

	@Override
	protected boolean skipRendering(
			@NotNull BlockState state,
			@NotNull BlockState adjacentState,
			@NotNull Direction direction
	) {
		return adjacentState.is(BarricadeTags.BlockTags.BARRIERS);
	}
}

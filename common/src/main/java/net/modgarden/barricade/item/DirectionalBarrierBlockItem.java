package net.modgarden.barricade.item;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import org.jetbrains.annotations.NotNull;

public class DirectionalBarrierBlockItem extends OperatorBlockItem<DirectionalBarrierBlock> {
	public DirectionalBarrierBlockItem(DirectionalBarrierBlock block, Properties properties) {
		super(block, properties);
	}

	// Make sure that no matter the directional context, block placements are consistently declined if an entity is in the block.
	@Override
	protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
		return super.canPlace(context, state) && (!mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos()));
	}
}

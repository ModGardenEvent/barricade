package net.modgarden.barricade.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DirectionalBarrierBlockItem extends BlockItem {
	public DirectionalBarrierBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	// Make sure that no matter the directional context, block placements are consistently declined if an entity is in the block.
	@Override
	protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
		return (!mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos()));
	}
}

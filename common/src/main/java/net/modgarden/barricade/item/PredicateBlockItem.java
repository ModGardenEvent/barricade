package net.modgarden.barricade.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.block.PredicateBlock;
import net.modgarden.silicate.api.exception.InvalidContextParameterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PredicateBlockItem<T extends Block, S extends PredicateBlock<T>> extends BlockItem {
	public PredicateBlockItem(S block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
		if (super.canPlace(context, state) && context.getPlayer() != null) {
			try {
				return this.getBlock().test(context.getLevel(), context.getPlayer(), state, context.getClickedPos());
			} catch (InvalidContextParameterException e) {
				throw new RuntimeException(e);
			}
		} else {
			return false;
		}
	}

	@Override
	protected @Nullable BlockState getPlacementState(@NotNull BlockPlaceContext context) {
		return super.getPlacementState(context);
	}

	@SuppressWarnings("unchecked") // It is illegal to have a this.block field not of type S
	@Override
	public @NotNull S getBlock() {
		return (S) super.getBlock();
	}
}

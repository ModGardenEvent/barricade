package net.modgarden.barricade.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.block.PredicateBlock;
import org.jetbrains.annotations.NotNull;

public class PredicateBlockItem<T extends Block & PredicateBlock> extends BlockItem {
	public PredicateBlockItem(T block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
		if (super.canPlace(context, state) && context.getPlayer() != null) {
			return context.getPlayer().getAbilities().instabuild;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked") // It is illegal to have a this.block field not of type T
	@Override
	public @NotNull T getBlock() {
		return (T) super.getBlock();
	}
}

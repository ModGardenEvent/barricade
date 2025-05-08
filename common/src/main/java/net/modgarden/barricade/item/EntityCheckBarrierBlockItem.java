package net.modgarden.barricade.item;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;

public class EntityCheckBarrierBlockItem<T extends Block> extends OperatorBlockItem<T> {
	public EntityCheckBarrierBlockItem(T block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
		return super.canPlace(context, state) && (!mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().getEntities(null, Shapes.block().move(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ()).bounds()).stream().allMatch(entity ->
				state.getCollisionShape(context.getLevel(), context.getClickedPos(), CollisionContext.of(entity)).isEmpty());
	}
}

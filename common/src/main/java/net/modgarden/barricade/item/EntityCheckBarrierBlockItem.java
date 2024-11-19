package net.modgarden.barricade.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;

public class EntityCheckBarrierBlockItem extends BlockItem {
    public EntityCheckBarrierBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        return (!mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().getEntities(null, Shapes.block().move(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ()).bounds()).stream().allMatch(entity ->
                !state.getCollisionShape(context.getLevel(), context.getClickedPos(), CollisionContext.of(entity)).isEmpty());
    }
}

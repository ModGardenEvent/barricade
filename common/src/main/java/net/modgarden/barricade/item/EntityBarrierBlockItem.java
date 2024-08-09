package net.modgarden.barricade.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.modgarden.barricade.block.EntityBarrierBlock;

public class EntityBarrierBlockItem extends BlockItem {
    public EntityBarrierBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        return context.getLevel().getEntities(null, Shapes.block().bounds().move(context.getClickedPos())).stream()
                .noneMatch(entity -> {
                    CollisionContext collisionContext = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
                    if (state.getBlock() instanceof EntityBarrierBlock entityBarrier)
                        return !entityBarrier.entities().canPass(collisionContext);
                    return false;
                });
    }
}

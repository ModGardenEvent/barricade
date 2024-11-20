package net.modgarden.barricade.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.modgarden.barricade.registry.BarricadeComponents;

public class AdvancedBarrierBlockItem extends BlockItem {
    public AdvancedBarrierBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    // Place with the entities in the block in mind instead of the placer's collision.
    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        return (!mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().getEntities(null, Shapes.block().move(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ()).bounds()).stream().allMatch(entity ->
                context.getItemInHand().has(BarricadeComponents.BLOCKED_ENTITIES) && context.getItemInHand().get(BarricadeComponents.BLOCKED_ENTITIES).canPass(CollisionContext.of(entity)));
    }
}

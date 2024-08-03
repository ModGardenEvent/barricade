package net.modgarden.barricade.item;

import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.modgarden.barricade.component.BlockedEntitiesComponent;
import net.modgarden.barricade.registry.BarricadeComponents;

public class EntityBarrierBlockItem extends ItemNameBlockItem {
    public EntityBarrierBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        return context.getLevel().getEntities(null, Shapes.block().bounds().move(context.getClickedPos())).stream()
                .noneMatch(entity -> {
                    CollisionContext collisionContext = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
                    return !context.getItemInHand().getOrDefault(BarricadeComponents.BLOCKED_ENTITIES, BlockedEntitiesComponent.EMPTY).canPass(collisionContext);
                });
    }
}

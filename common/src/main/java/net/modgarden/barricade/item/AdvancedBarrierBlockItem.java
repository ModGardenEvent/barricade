package net.modgarden.barricade.item;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeComponents;

import java.util.HashSet;
import java.util.Set;

public class AdvancedBarrierBlockItem extends EntityCheckBarrierBlockItem {
    public AdvancedBarrierBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        ItemStack stack = context.getItemInHand();
        boolean collision;
        if (!stack.has(BarricadeComponents.ADVANCED_BARRIER)) {
            CollisionContext ctx = context.getPlayer() == null ? CollisionContext.empty() : CollisionContext.of(context.getPlayer());
            collision = context.getLevel().isUnobstructed(state, context.getClickedPos(), ctx);
        } else {
            AdvancedBarrier advancedBarrier = stack.get(BarricadeComponents.ADVANCED_BARRIER).value();
            ServerLevel serverLevel;
            if (context.getLevel() instanceof ServerLevel)
                serverLevel = (ServerLevel) context.getLevel();
            else
                serverLevel = null;

            BlockedDirections directions = directions(advancedBarrier.directions(), BarricadeBlocks.ADVANCED_BARRIER.directions(state));

            collision = context.getLevel().getEntities(null, Shapes.block().move(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ()).bounds()).stream().allMatch(entity -> {
                CollisionContext ctx = CollisionContext.of(entity);
                return (advancedBarrier.condition().isEmpty() || !advancedBarrier.test(serverLevel, entity, state, context.getClickedPos()) && (directions.doesNotBlock() || !directions.shouldBlock(context.getClickedPos(), ctx)));
            });
        }

        return (!mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos())) && collision;
    }

    private static BlockedDirections directions(BlockedDirections directions, BlockedDirections stateDirections) {
        Set<Direction> returnValue = new HashSet<>();
        processDirection(returnValue, Direction.UP, directions, stateDirections);
        processDirection(returnValue, Direction.DOWN, directions, stateDirections);
        processDirection(returnValue, Direction.NORTH, directions, stateDirections);
        processDirection(returnValue, Direction.SOUTH, directions, stateDirections);
        processDirection(returnValue, Direction.WEST, directions, stateDirections);
        processDirection(returnValue, Direction.EAST, directions, stateDirections);
        return BlockedDirections.of(returnValue.toArray(Direction[]::new));
    }

    private static void processDirection(Set<Direction> set, Direction direction, BlockedDirections directions, BlockedDirections stateDirections) {
        if (stateDirections.directions().contains(direction) || directions.blocks(direction))
            set.add(direction);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.has(BarricadeComponents.ADVANCED_BARRIER) && stack.get(BarricadeComponents.ADVANCED_BARRIER).value().name().isPresent())
            return stack.get(BarricadeComponents.ADVANCED_BARRIER).value().name().get();
        return super.getName(stack);
    }
}

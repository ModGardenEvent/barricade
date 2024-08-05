package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.block.entity.DirectionalBarrierBlockEntity;
import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DirectionalBarrierBlock extends BarrierBlock implements EntityBlock {
    public static final MapCodec<BarrierBlock> CODEC = simpleCodec(DirectionalBarrierBlock::new);

    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    public DirectionalBarrierBlock(Properties properties) {
        super(properties);
        registerDefaultState(getDefaultState(getStateDefinition()));
    }

    private static BlockState getDefaultState(StateDefinition<Block, BlockState> stateDefinition) {
        BlockState blockstate = stateDefinition.any();

        for (BooleanProperty booleanproperty : PROPERTY_BY_DIRECTION.values()) {
            if (blockstate.hasProperty(booleanproperty)) {
                blockstate = blockstate.setValue(booleanproperty, Boolean.FALSE);
            }
        }

        if (blockstate.hasProperty(WATERLOGGED))
             blockstate = blockstate.setValue(WATERLOGGED, Boolean.FALSE);

        return blockstate;
    }


    @Override
    public MapCodec<BarrierBlock> codec() {
        return CODEC;
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        if (!stack.has(BarricadeComponents.BLOCKED_DIRECTIONS))
            return this.defaultBlockState();

        var component = stack.getOrDefault(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.EMPTY);
        return this.defaultBlockState()
                .setValue(DOWN, component.blocks(Direction.DOWN))
                .setValue(UP, component.blocks(Direction.UP))
                .setValue(NORTH, component.blocks(Direction.NORTH))
                .setValue(EAST, component.blocks(Direction.EAST))
                .setValue(SOUTH, component.blocks(Direction.SOUTH))
                .setValue(WEST, component.blocks(Direction.WEST));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockedDirectionsComponent component = BlockedDirectionsComponent.fromBlockState(state);
        if (component.doesNotBlock())
            return Shapes.empty();
        if (component.blocksAll())
            return super.getCollisionShape(state, level, pos, context);
        Direction direction = component.blockingDirection(pos, context);
        if (direction == null)
            return Shapes.empty();
        return Shapes.block().getFaceShape(direction);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.NORTH)), state.getValue(NORTH))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.SOUTH)), state.getValue(SOUTH))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.EAST)), state.getValue(EAST))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.WEST)), state.getValue(WEST))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.UP)), state.getValue(UP))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.DOWN)), state.getValue(DOWN));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.NORTH)), state.getValue(NORTH))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.SOUTH)), state.getValue(SOUTH))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.EAST)), state.getValue(EAST))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.WEST)), state.getValue(WEST))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.UP)), state.getValue(UP))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.DOWN)), state.getValue(DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DirectionalBarrierBlockEntity(pos, state);
    }
}

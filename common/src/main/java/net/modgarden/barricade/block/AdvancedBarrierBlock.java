package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdvancedBarrierBlock extends BarrierBlock implements EntityBlock {
    public static final MapCodec<BarrierBlock> CODEC = simpleCodec(AdvancedBarrierBlock::new);

    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    public AdvancedBarrierBlock(Properties properties) {
        super(properties);
        defaultBlockState()
                .setValue(UP, true)
                .setValue(DOWN, true)
                .setValue(NORTH, true)
                .setValue(EAST, true)
                .setValue(SOUTH, true)
                .setValue(WEST, true);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        if (!stack.has(BarricadeComponents.ADVANCED_BARRIER))
            return defaultBlockState();
        Holder<AdvancedBarrier> advancedBarrier = stack.get(BarricadeComponents.ADVANCED_BARRIER);
        return defaultBlockState()
                .setValue(DOWN, advancedBarrier.value().directions().blocks(Direction.DOWN))
                .setValue(UP, advancedBarrier.value().directions().blocks(Direction.UP))
                .setValue(NORTH, advancedBarrier.value().directions().blocks(Direction.NORTH))
                .setValue(SOUTH, advancedBarrier.value().directions().blocks(Direction.SOUTH))
                .setValue(WEST, advancedBarrier.value().directions().blocks(Direction.WEST))
                .setValue(EAST, advancedBarrier.value().directions().blocks(Direction.EAST));
    }

    public BlockedDirections directions(BlockState state) {
        Set<Direction> directions = new HashSet<>();
        if (state.getValue(UP))
            directions.add(Direction.UP);
        if (state.getValue(DOWN))
            directions.add(Direction.DOWN);
        if (state.getValue(NORTH))
            directions.add(Direction.NORTH);
        if (state.getValue(SOUTH))
            directions.add(Direction.SOUTH);
        if (state.getValue(WEST))
            directions.add(Direction.WEST);
        if (state.getValue(EAST))
            directions.add(Direction.EAST);
        return BlockedDirections.of(directions.toArray(Direction[]::new));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public MapCodec<BarrierBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof AdvancedBarrierBlockEntity blockEntity) {
            ServerLevel serverLevel = null;
            if (level instanceof ServerLevel)
                serverLevel = (ServerLevel) level;
            if ((blockEntity.getData().condition().isEmpty() || context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null && blockEntity.getData().test(serverLevel, entityContext.getEntity(), state, pos)) && (directions(state).blocksAll() || directions(state).shouldBlock(pos, context)))
                return Shapes.block();
        }
        return Shapes.empty();
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof AdvancedBarrierBlockEntity blockEntity && context instanceof EntityCollisionContext entityContext) {
            ServerLevel serverLevel = null;
            if (level instanceof ServerLevel)
                serverLevel = (ServerLevel) level;
            if (entityContext.getEntity() instanceof Player player && player.canUseGameMasterBlocks() || entityContext.getEntity() != null && blockEntity.getData().test(serverLevel, entityContext.getEntity(), state, pos))
                return super.getShape(state, level, pos, context);
        }
        return Shapes.empty();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof AdvancedBarrierBlockEntity blockEntity)
            stack.set(BarricadeComponents.ADVANCED_BARRIER, blockEntity.getHolder());
        return stack;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedBarrierBlockEntity(pos, state);
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
}

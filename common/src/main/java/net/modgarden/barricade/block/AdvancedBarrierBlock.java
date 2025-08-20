package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.registry.BarricadeComponents;
import net.modgarden.barricade.registry.BarricadeRegistries;
import net.modgarden.silicate.api.exception.InvalidContextParameterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
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
		if (advancedBarrier != null) {
			return defaultBlockState()
					.setValue(DOWN, advancedBarrier.value().directions().blocks(Direction.DOWN))
					.setValue(UP, advancedBarrier.value().directions().blocks(Direction.UP))
					.setValue(NORTH, advancedBarrier.value().directions().blocks(Direction.NORTH))
					.setValue(SOUTH, advancedBarrier.value().directions().blocks(Direction.SOUTH))
					.setValue(WEST, advancedBarrier.value().directions().blocks(Direction.WEST))
					.setValue(EAST, advancedBarrier.value().directions().blocks(Direction.EAST));
		} else {
			return defaultBlockState()
					.setValue(UP, true)
					.setValue(DOWN, true)
					.setValue(NORTH, true)
					.setValue(EAST, true)
					.setValue(SOUTH, true)
					.setValue(WEST, true);
		}
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
	public @NotNull MapCodec<BarrierBlock> codec() {
		return CODEC;
	}

	public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
		return neighborState.is(state.getBlock()) && (!(level.getBlockEntity(pos) instanceof AdvancedBarrierBlockEntity blockEntity) || level.getBlockEntity(pos.offset(dir.getOpposite().getUnitVec3i())) instanceof AdvancedBarrierBlockEntity neighborEntity && neighborEntity.getData().equals(blockEntity.getData()) || directions(state) == null || !directions(state).blocks(dir));
	}

	@Override
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		if (blockGetter.getBlockEntity(pos) instanceof AdvancedBarrierBlockEntity blockEntity) {
			Level level = null;
			if (blockGetter instanceof Level) {
				level = (Level) blockGetter;
			}

			try {
				boolean meetsCondition = blockEntity.getData().condition().isEmpty()
						|| context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null && blockEntity.getData()
						.test(level, entityContext.getEntity(), state, pos);
				boolean blocksDirection = directions(state).blocksAll() || directions(state).shouldBlock(pos, context);
				if (!meetsCondition || !blocksDirection) {
					return Shapes.empty();
				}
			} catch (InvalidContextParameterException e) {
				Barricade.LOG.error("Failed to test shape", e);
			}
		}
		return Shapes.block();
	}


	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		if (blockGetter.getBlockEntity(pos) instanceof AdvancedBarrierBlockEntity blockEntity && context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null) {
			Level level = null;
			if (blockGetter instanceof Level) {
				level = (Level) blockGetter;
			}

			try {
				boolean isOperator = entityContext.getEntity() instanceof Player player && player.getAbilities().instabuild;
				if (!isOperator && !blockEntity.getData().test(level, entityContext.getEntity(), state, pos)) {
					return Shapes.empty();
				}
			} catch (InvalidContextParameterException e) {
				Barricade.LOG.error("Failed to test shape", e);
			}
		}
		return super.getShape(state, blockGetter, pos, context);
	}

	@Override
	public @NotNull ItemStack getCloneItemStack(LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean includeData) {
		ItemStack stack = new ItemStack(this);
		if (level.getBlockEntity(pos) instanceof AdvancedBarrierBlockEntity blockEntity) {
			Holder<AdvancedBarrier> data = blockEntity.getHolder();
			// Switch to default barrier
			if (blockEntity.getData().equals(AdvancedBarrier.DEFAULT)) {
				data = Objects.requireNonNull(level).registryAccess()
						.getOrThrow(BarricadeRegistries.ADVANCED_BARRIER)
						.value()
						.getOrThrow(ResourceKey.create(
								BarricadeRegistries.ADVANCED_BARRIER,
								Barricade.asResource("default")
						));
			}

			stack.set(BarricadeComponents.ADVANCED_BARRIER, data);
		}
		return stack;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new AdvancedBarrierBlockEntity(pos, state);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected @NotNull BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.NORTH)), state.getValue(NORTH))
				.setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.SOUTH)), state.getValue(SOUTH))
				.setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.EAST)), state.getValue(EAST))
				.setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.WEST)), state.getValue(WEST))
				.setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.UP)), state.getValue(UP))
				.setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.DOWN)), state.getValue(DOWN));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.NORTH)), state.getValue(NORTH))
				.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.SOUTH)), state.getValue(SOUTH))
				.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.EAST)), state.getValue(EAST))
				.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.WEST)), state.getValue(WEST))
				.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.UP)), state.getValue(UP))
				.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.DOWN)), state.getValue(DOWN));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
	}
}

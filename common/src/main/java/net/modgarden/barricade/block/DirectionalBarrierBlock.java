package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.data.BlockedDirections;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DirectionalBarrierBlock extends StaticBarrierBlock {
	public static final MapCodec<DirectionalBarrierBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			BlockedDirections.CODEC.fieldOf("directions").forGetter(DirectionalBarrierBlock::directions),
			propertiesCodec()
	).apply(inst, DirectionalBarrierBlock::new));
	private static final Map<Direction, DirectionalBarrierBlock> DIRECTION_MAP = new HashMap<>() {
		@Override
		public DirectionalBarrierBlock put(Direction key, DirectionalBarrierBlock value) {
			if (containsKey(key))
				throw new RuntimeException("Cannot add direction '" + key.getName() + "' to map when it has already been added.");
			return super.put(key, value);
		}

		@Override
		public void putAll(Map<? extends Direction, ? extends DirectionalBarrierBlock> m) {
			if (m.keySet().stream().anyMatch(m::containsKey))
				throw new RuntimeException("Cannot add directions to map when one has already been added.");
			super.putAll(m);
		}
	};

	private final BlockedDirections directions;

	public DirectionalBarrierBlock(BlockedDirections directions, Properties properties) {
		super(properties);
		this.directions = directions;
		if (directions.directions().size() == 1)
			DIRECTION_MAP.put(directions.directions().stream().findFirst().get(), this);
	}

	public DirectionalBarrierBlock(Properties properties, Direction ...blockedDirections) {
		this(BlockedDirections.of(blockedDirections), properties);
	}

	public BlockedDirections directions() {
		return directions;
	}

	@Override
	public @NotNull MapCodec<BarrierBlock> codec() {
		return CODEC.xmap(dir -> dir, barrierBlock -> (DirectionalBarrierBlock) barrierBlock);
	}

	@Override
	protected boolean skipRendering(BlockState state, BlockState adjacentState, @NotNull Direction direction) {
		return adjacentState.is(state.getBlock()) && !directions.blocks(direction);
	}

	@Override
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		if (!directions.doesNotBlock() && (directions.blocksAll() || directions.shouldBlock(pos, context)))
			return Shapes.block();
		return Shapes.empty();
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		boolean isOperator = context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Player player && player.getAbilities().instabuild;
		if (isOperator || !directions.doesNotBlock() && (directions.blocksAll() || directions.shouldBlock(pos, context)))
			return super.getShape(state, level, pos, context);
		return Shapes.empty();
	}

	@Override
	protected @NotNull BlockState rotate(BlockState state, @NotNull Rotation rot) {
		if (!(state.getBlock() instanceof DirectionalBarrierBlock directional) || directional.directions.directions().size() != 1)
			return state;
		return DIRECTION_MAP.get(rot.rotate(directional.directions.directions().stream().findFirst().get())).defaultBlockState();
	}

	@Override
	protected @NotNull BlockState mirror(BlockState state, @NotNull Mirror mirror) {
		if (!(state.getBlock() instanceof DirectionalBarrierBlock directional) || directional.directions.directions().size() != 1)
			return state;
		return DIRECTION_MAP.get(mirror.mirror(directional.directions.directions().stream().findFirst().get())).defaultBlockState();
	}

	@Override
	public BlockedDirections getBlockedDirections() {
		return this.directions;
	}

	@Override
	public ResourceLocation getIcon() {
		return null;
	}
}

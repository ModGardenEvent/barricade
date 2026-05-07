package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.attachment.BarricadePalette;
import net.modgarden.barricade.attachment.ModAttachments;
import net.modgarden.barricade.data.BarricadeData;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

public class BarricadeBlock extends BarrierBlock {
	public static final MapCodec<BarrierBlock> CODEC = simpleCodec(BarricadeBlock::new);

	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;

	public BarricadeBlock(Properties properties) {
		super(properties);
		defaultBlockState()
				.setValue(UP, true)
				.setValue(DOWN, true)
				.setValue(NORTH, true)
				.setValue(EAST, true)
				.setValue(SOUTH, true)
				.setValue(WEST, true);
	}

	public static @NonNull BarricadeData getBarricadeData(
			@NonNull BlockPos pos,
			Level level
	) {
		return getBarricadeDataHolder(pos, level).value();
	}

	private static @NonNull Holder<BarricadeData> getBarricadeDataHolder(
			@NonNull BlockPos pos,
			Level level
	) {
		LevelChunk chunk = level.getChunkAt(pos);
		Int2ObjectMap<BarricadePalette> map = chunk.getAttachedOrCreate(ModAttachments.BARRICADE_PALETTE, Int2ObjectOpenHashMap::new);
		BarricadePalette palette = map.computeIfAbsent(chunk.getSectionIndex(pos.getY()), _ -> new BarricadePalette(new PalettedContainer<>(BarricadeData.DEFAULT_HOLDER, BarricadeData.STRATEGY)));
		return palette.palettedContainer().get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
	}

	public static void setBarricadeData(
			Level level,
			BlockPos pos,
			Holder<BarricadeData> barricadeDataHolder
	) {
		LevelChunk chunk = level.getChunkAt(pos);
		Int2ObjectMap<BarricadePalette> map = chunk.getAttachedOrCreate(
				ModAttachments.BARRICADE_PALETTE,
				Int2ObjectOpenHashMap::new
		);
		BarricadePalette palette = map.computeIfAbsent(
				chunk.getSectionIndex(pos.getY()),
				_ -> new BarricadePalette(new PalettedContainer<>(
						BarricadeData.DEFAULT_HOLDER,
						BarricadeData.STRATEGY
				))
		);
		palette.palettedContainer().set(
				pos.getX() & 15,
				pos.getY() & 15,
				pos.getZ() & 15, barricadeDataHolder
		);

		if (!level.isClientSide()) {
			chunk.setAttached(ModAttachments.BARRICADE_PALETTE, null); // FIXME: ugly hack to force sync
			chunk.setAttached(ModAttachments.BARRICADE_PALETTE, map);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		ItemStack stack = context.getItemInHand();
		if (!stack.has(BarricadeComponents.BARRICADE))
			return defaultBlockState();
		Holder<BarricadeData> advancedBarrier = stack.get(BarricadeComponents.BARRICADE);
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

	@Override
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		if (!(blockGetter instanceof Level level)) return Shapes.block();

		BarricadeData data = getBarricadeData(pos, level);

		try {
			boolean meetsCondition = data.condition().isEmpty()
					|| context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null && data
					.test(level, entityContext.getEntity(), state, pos);
			boolean blocksDirection = directions(state).blocksAll() || directions(state).shouldBlock(pos, context);
			if (!meetsCondition || !blocksDirection) {
				return Shapes.empty();
			}
		} catch (Exception e) {
			BarricadeMod.LOG.error("Failed to test shape", e);
		}

		return Shapes.block();
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null) {
			if (!(blockGetter instanceof Level level)) return Shapes.block();

			try {
				BarricadeData data = getBarricadeData(pos, level);
				boolean isOperator = entityContext.getEntity() instanceof Player player && player.getAbilities().instabuild;
				if (!isOperator && !data.test(level, entityContext.getEntity(), state, pos)) {
					return Shapes.empty();
				}
			} catch (Exception e) {
				BarricadeMod.LOG.error("Failed to test shape", e);
			}
		}

		return super.getShape(state, blockGetter, pos, context);
	}

	@Override
	public @NotNull ItemStack getCloneItemStack(LevelReader levelReader, @NotNull BlockPos pos, @NotNull BlockState state, boolean includeData) {
		if (!(levelReader instanceof Level level)) return new ItemStack(Items.BARRIER);

		ItemStack stack = new ItemStack(this);
		Holder<BarricadeData> data = getBarricadeDataHolder(pos, level);

		// Switch to default barrier
		if (data.unwrapKey().orElseThrow().equals(BarricadeData.DEFAULT_HOLDER.unwrapKey().orElseThrow())) {
			return new ItemStack(Items.BARRIER);
		}

		stack.set(BarricadeComponents.BARRICADE, data);

		return stack;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
	}
}

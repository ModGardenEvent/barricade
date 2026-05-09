package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lgbt.greenhouse.silicate.api.context.GameContext;
import lgbt.greenhouse.silicate.api.context.parameter.GlobalParameterKeys;
import lgbt.greenhouse.silicate.api.context.parameter.ParameterMap;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.attachment.BarricadePalette;
import net.modgarden.barricade.attachment.ModAttachments;
import net.modgarden.barricade.block.entity.BarricadeBlockEntity;
import net.modgarden.barricade.data.BarricadeData;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.network.clientbound.ClientboundSyncBarricadeDataPayload;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class BarricadeBlock extends BarrierBlock implements EntityBlock {
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
		Holder<BarricadeData> holder = getBarricadeDataHolder(pos, level);

		if (!holder.isBound()) {
			return BarricadeData.UNKNOWN;
		}

		return holder.value();
	}

	public static @NonNull Holder<BarricadeData> getBarricadeDataHolder(
			@NonNull BlockPos pos,
			Level level
	) {
		return getBarricadeDataHolder(pos, level, false);
	}

	public static @NonNull Holder<BarricadeData> getBarricadeDataHolder(
			@NonNull BlockPos pos,
			Level level,
			boolean fromBlockEntity
	) {
		LevelChunk chunk = level.getChunkAt(pos);
		Int2ObjectMap<BarricadePalette> map = chunk.getAttachedOrCreate(ModAttachments.BARRICADE_PALETTE, Int2ObjectOpenHashMap::new);
		BarricadePalette palette = map.computeIfAbsent(chunk.getSectionIndex(pos.getY()), _ -> new BarricadePalette(new PalettedContainer<>(BarricadeData.UNKNOWN_HOLDER, BarricadeData.STRATEGY)));

		Holder<BarricadeData> holder = palette.palettedContainer().get(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);

		if (holder.equals(BarricadeData.UNKNOWN_HOLDER) && !fromBlockEntity) {
			Optional<BarricadeBlockEntity> optionalBlockEntity = chunk.getBlockEntity(pos, BarricadeBlockEntityTypes.BARRICADE);

			if (optionalBlockEntity.isPresent()) {
				Holder<BarricadeData> holder1 = BarricadeData.ID_MAPPER.byId(optionalBlockEntity.get().id);

				if (holder1 == null) {
					return BarricadeData.UNKNOWN_HOLDER;
				}

				return holder1;
			}
		}

		return holder;
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
						BarricadeData.UNKNOWN_HOLDER,
						BarricadeData.STRATEGY
				))
		);
		palette.palettedContainer().set(
				pos.getX() & 15,
				pos.getY() & 15,
				pos.getZ() & 15, barricadeDataHolder
		);

		if (!level.isClientSide()) {
			chunk.setAttached(ModAttachments.BARRICADE_PALETTE, map);

			// Note that this doesn't matter for large servers when you aren't setting/modifying any barricades
			for (Player player : level.players()) {
				ServerPlayNetworking.send((ServerPlayer) player, new ClientboundSyncBarricadeDataPayload(pos, BarricadeData.ID_MAPPER.getId(barricadeDataHolder)));
			}
		}
	}

	public static GameContext newContext(
			@NotNull Level level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) {
		ParameterMap paramMap = ParameterMap.Builder
				.of()
				.withParameter(GlobalParameterKeys.THIS_ENTITY, entity)
				.withParameter(GlobalParameterKeys.BLOCK_STATE, state)
				.withParameter(GlobalParameterKeys.ORIGIN, pos.getCenter())
				.build();
		return GameContext.of(level, paramMap);
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
		if (data.unwrapKey().orElseThrow().equals(BarricadeData.UNKNOWN_HOLDER.unwrapKey().orElseThrow())) {
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

	@Override
	public @Nullable BlockEntity newBlockEntity(
			@NonNull BlockPos worldPosition,
			@NonNull BlockState blockState
	) {
		return new BarricadeBlockEntity(worldPosition, blockState);
	}
}

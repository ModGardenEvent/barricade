package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.mixin.BlockAccessor;
import net.modgarden.barricade.mixin.FaceAttachedHorizontalDirectionalBlockAccessor;
import net.modgarden.barricade.mixin.HorizontalDirectionalBlockAccessor;
import net.modgarden.barricade.mixin.LeverBlockAccessor;
import net.modgarden.silicate.api.condition.GameCondition;
import net.modgarden.silicate.api.exception.InvalidContextParameterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class PredicateLeverBlock extends PredicateBlock<LeverBlock> {
	private static final LeverBlock DEFAULT_SIMULATED = (LeverBlock) Blocks.LEVER;
	public static final Properties PROPERTIES = Properties.ofFullCopy(Blocks.LEVER);

	public PredicateLeverBlock(ResourceLocation icon, ResourceKey<GameCondition<?>> conditionTemplate) {
		super(LeverBlockAccessor.init(PROPERTIES), PROPERTIES, icon, conditionTemplate);
		registerState();
	}

	protected PredicateLeverBlock(Properties properties, ResourceLocation icon, Holder<GameCondition<?>> condition) {
		super(LeverBlockAccessor.init(properties), properties, icon, condition);
		registerState();
	}

	private void registerState() {
		this.registerDefaultState(
				this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH).setValue(LeverBlock.POWERED, Boolean.FALSE).setValue(LeverBlock.FACE, AttachFace.WALL)
		);
		((BlockAccessor) simulated()).setDefaultBlockState(this.defaultBlockState());
		((BlockAccessor) simulated()).setBuiltInRegistryHolder(((BlockAccessor) this).getBuiltInRegistryHolder());
	}

	@Override
	protected @NotNull MapCodec<? extends Block> codec() {
		return mapCodec(PredicateLeverBlock::new);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
		return ((HorizontalDirectionalBlockAccessor) simulated()).invokeRotate(state, rotation);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
		return ((HorizontalDirectionalBlockAccessor) simulated()).invokeMirror(state, mirror);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		return simulated().getStateForPlacement(context);
	}

	@Override
	protected @NotNull BlockState updateShape(
			@NotNull BlockState state,
			@NotNull Direction direction,
			@NotNull BlockState neighborState,
			@NotNull LevelAccessor level,
			@NotNull BlockPos pos,
			@NotNull BlockPos neighborPos
	) {
		return ((FaceAttachedHorizontalDirectionalBlockAccessor) simulated()).invokeUpdateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected @NotNull VoxelShape getShape(
			@NotNull BlockState state,
			@NotNull BlockGetter blockGetter,
			@NotNull BlockPos pos,
			@NotNull CollisionContext context
	) {
		if (this.isShaped(state, blockGetter, pos, context)) {
			return ((LeverBlockAccessor) simulated()).invokeGetShape(state, blockGetter, pos, context);
		} else {
			return Shapes.empty();
		}
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(
			@NotNull BlockState state,
			@NotNull Level level,
			@NotNull BlockPos pos,
			@NotNull Player player,
			@NotNull BlockHitResult hitResult
	) {
		try {
			if (level.isClientSide() || test(level, player, state, pos)) {
				return ((LeverBlockAccessor) simulated()).invokeUseWithoutItem(state, level, pos, player, hitResult);
			} else {
				return InteractionResult.PASS;
			}
		} catch (InvalidContextParameterException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void onExplosionHit(
			@NotNull BlockState state,
			@NotNull Level level,
			@NotNull BlockPos pos,
			@NotNull Explosion explosion,
			@NotNull BiConsumer<ItemStack, BlockPos> dropConsumer
	) {
		((LeverBlockAccessor) simulated()).invokeOnExplosionHit(state, level, pos, explosion, dropConsumer);
	}

	@Override
	public void animateTick(
			@NotNull BlockState state,
			@NotNull Level level,
			@NotNull BlockPos pos,
			@NotNull RandomSource random
	) {
		((LeverBlockAccessor) simulated()).invokeAnimateTick(state, level, pos, random);
	}

	@Override
	protected void onRemove(
			@NotNull BlockState state,
			@NotNull Level level,
			@NotNull BlockPos pos,
			@NotNull BlockState newState,
			boolean movedByPiston
	) {
		((LeverBlockAccessor) simulated()).invokeOnRemove(state, level, pos, newState, movedByPiston);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected int getSignal(
			@NotNull BlockState state,
			@NotNull BlockGetter level,
			@NotNull BlockPos pos,
			@NotNull Direction direction
	) {
		return ((LeverBlockAccessor) simulated()).invokeGetSignal(state, level, pos, direction);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected int getDirectSignal(
			@NotNull BlockState state,
			@NotNull BlockGetter level,
			@NotNull BlockPos pos,
			@NotNull Direction direction
	) {
		return ((LeverBlockAccessor) simulated()).invokeGetDirectSignal(state, level, pos, direction);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected boolean isSignalSource(@NotNull BlockState state) {
		return ((LeverBlockAccessor) simulated()).invokeIsSignalSource(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		((LeverBlockAccessor) DEFAULT_SIMULATED).invokeCreateBlockStateDefinition(builder);
	}
}

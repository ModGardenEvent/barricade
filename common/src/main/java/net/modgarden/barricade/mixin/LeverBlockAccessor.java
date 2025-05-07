package net.modgarden.barricade.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiConsumer;

@Mixin(LeverBlock.class)
public interface LeverBlockAccessor {
	@Invoker("<init>")
	static LeverBlock init(BlockBehaviour.Properties properties) {
		throw new UnsupportedOperationException("Mixin not applied");
	}

	@Invoker
	@NotNull VoxelShape invokeGetShape(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull CollisionContext context);

	@Invoker
	InteractionResult invokeUseWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult);

	@Invoker
	void invokeOnExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer);

	@Invoker
	void invokeAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource random);

	@Invoker
	void invokeOnRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving);

	@Invoker
	int invokeGetSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side);

	@Invoker
	int invokeGetDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side);

	@Invoker
	boolean invokeIsSignalSource(BlockState state);

	@Invoker
	void invokeCreateBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder);
}

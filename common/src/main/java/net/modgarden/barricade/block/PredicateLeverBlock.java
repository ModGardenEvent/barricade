package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.silicate.api.condition.GameCondition;
import net.modgarden.silicate.api.exception.InvalidContextParameterException;
import org.jetbrains.annotations.NotNull;

public class PredicateLeverBlock extends LeverBlock implements PredicateBlock {
	@SuppressWarnings("unused")
	public PredicateLeverBlock(Properties properties, ResourceLocation icon, ResourceKey<GameCondition<?>> conditionTemplate) {
		super(properties);
	}

	protected PredicateLeverBlock(Properties properties, ResourceLocation icon, Holder<GameCondition<?>> condition) {
		super(properties);
	}

	@Override
	public @NotNull MapCodec<LeverBlock> codec() {
		return PredicateBlock.mapCodec(PredicateLeverBlock::new);
	}

	@Override
	protected @NotNull VoxelShape getShape(
			@NotNull BlockState state,
			@NotNull BlockGetter blockGetter,
			@NotNull BlockPos pos,
			@NotNull CollisionContext context
	) {
		if (this.isShaped(state, blockGetter, pos, context)) {
			return super.getShape(state, blockGetter, pos, context);
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
			if (level.isClientSide() || this.barricade$test(level, player, state, pos)) {
				return super.useWithoutItem(state, level, pos, player, hitResult);
			} else {
				return InteractionResult.PASS;
			}
		} catch (InvalidContextParameterException e) {
			throw new RuntimeException(e);
		}
	}
}

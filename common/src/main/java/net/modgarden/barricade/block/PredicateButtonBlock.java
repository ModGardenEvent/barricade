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
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.silicate.api.condition.GameCondition;
import net.modgarden.silicate.api.exception.InvalidContextParameterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.modgarden.barricade.Barricade.LOG;

public class PredicateButtonBlock extends ButtonBlock implements PredicateBlock {
	@SuppressWarnings("unused")
	public PredicateButtonBlock(Properties properties, ResourceLocation icon, ResourceKey<GameCondition<?>> conditionTemplate) {
		super(BlockSetType.STONE, 20, properties);
	}

	protected PredicateButtonBlock(Properties properties, ResourceLocation icon, Holder<GameCondition<?>> condition) {
		super(BlockSetType.STONE, 20, properties);
	}

	@Override
	public @NotNull MapCodec<ButtonBlock> codec() {
		return PredicateBlock.mapCodec(PredicateButtonBlock::new);
	}

	@Override
	protected @NotNull VoxelShape getShape(
			@NotNull BlockState state,
			@NotNull BlockGetter level,
			@NotNull BlockPos pos,
			@NotNull CollisionContext context
	) {
		if (this.isShaped(state, level, pos, context)) {
			return super.getShape(state, level, pos, context);
		} else {
			return Shapes.empty();
		}
	}

	@Override
	public void press(
			@NotNull BlockState state,
			@NotNull Level level,
			@NotNull BlockPos pos,
			@Nullable Player player
	) {
		if (player == null) return;

		try {
			if (this.barricade$test(level, player, state, pos)) {
				super.press(state, level, pos, player);
			}
		} catch (InvalidContextParameterException e) {
			LOG.error("Failed to test condition", e);
		}
	}
}

package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.silicate.api.condition.GameCondition;
import org.jetbrains.annotations.NotNull;

/**
 * A type of {@link BarrierBlock} that uses {@link GameCondition} to determine if an entity collides.
 */
public class PredicateBarrierBlock extends BarrierBlock implements PredicateBlock {
	@SuppressWarnings("unused")
	public PredicateBarrierBlock(Properties properties, ResourceLocation icon, ResourceKey<GameCondition<?>> conditionTemplate) {
		super(properties);
	}

	private PredicateBarrierBlock(Properties properties, ResourceLocation icon, Holder<GameCondition<?>> condition) {
		super(properties);
	}

	@Override
	public @NotNull MapCodec<BarrierBlock> codec() {
		return PredicateBlock.mapCodec(PredicateBarrierBlock::new);
	}

	@Override
	protected boolean skipRendering(BlockState state, BlockState adjacentState, @NotNull Direction direction) {
		return adjacentState.is(state.getBlock());
	}

	@Override
	protected @NotNull VoxelShape getCollisionShape(
			@NotNull BlockState state,
			@NotNull BlockGetter blockGetter,
			@NotNull BlockPos pos,
			@NotNull CollisionContext context
	) {
		if (!this.isShaped(state, blockGetter, pos, context)) {
			return Shapes.block();
		} else {
			return Shapes.empty();
		}
	}

	@Override
	protected @NotNull VoxelShape getShape(
			@NotNull BlockState state,
			@NotNull BlockGetter blockGetter,
			@NotNull BlockPos pos,
			@NotNull CollisionContext context
	) {
		if (context instanceof EntityCollisionContext entityContext &&
				entityContext.getEntity() instanceof Player player &&
				player.getAbilities().instabuild) {
			return super.getShape(state, blockGetter, pos, context);
		} else {
			return Shapes.empty();
		}
	}
}

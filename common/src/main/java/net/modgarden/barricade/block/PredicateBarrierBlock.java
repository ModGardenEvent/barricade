package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import house.greenhouse.silicate.api.condition.CompoundCondition;
import house.greenhouse.silicate.api.condition.GameCondition;
import house.greenhouse.silicate.api.context.GameContext;
import house.greenhouse.silicate.api.context.param.ContextParamMap;
import house.greenhouse.silicate.api.context.param.ContextParamSet;
import house.greenhouse.silicate.api.context.param.ContextParamTypes;
import house.greenhouse.silicate.api.exception.InvalidContextParameterException;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.Barricade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A type of {@link BarrierBlock} that uses {@link GameCondition} to determine if an entity collides.
 */
public class PredicateBarrierBlock extends BarrierBlock {
	private static final MapCodec<PredicateBarrierBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			propertiesCodec(),
			ResourceLocation.CODEC
					.fieldOf("icon")
					.forGetter(PredicateBarrierBlock::icon),
			CompoundCondition.CODEC
					.fieldOf("conditions")
					.forGetter(PredicateBarrierBlock::condition)
	).apply(instance, PredicateBarrierBlock::new));
	public static final ContextParamSet PARAM_SET = ContextParamSet.Builder
			.of()
			.required(ContextParamTypes.THIS_ENTITY)
			.required(ContextParamTypes.BLOCK_STATE)
			.required(ContextParamTypes.ORIGIN)
			.build();
	private final ResourceLocation icon;
	private final CompoundCondition condition;

	public PredicateBarrierBlock(Properties properties, ResourceLocation icon, GameCondition<?>... conditions) {
		this(properties, icon, CompoundCondition.of(conditions));
	}
	
	private PredicateBarrierBlock(Properties properties, ResourceLocation icon, CompoundCondition condition) {
		super(properties);
		this.icon = icon;
		this.condition = condition;
	}

	public ResourceLocation icon() {
		return icon;
	}
	
	public CompoundCondition condition() {
		return condition;
	}
	
	@Override
	public @NotNull MapCodec<BarrierBlock> codec() {
			return CODEC.xmap(
				properties -> properties,
				block -> (PredicateBarrierBlock) block
			);
	}
	
	public boolean test(
			@Nullable Level level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) throws InvalidContextParameterException {
		return test(newContext(level, entity, state, pos));
	}
	
	public boolean test(GameContext context) {
		return this.condition().test(context);
	}
	
	public static GameContext newContext(
			@Nullable Level level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) throws InvalidContextParameterException {
		ContextParamMap paramMap = ContextParamMap.Builder
				.of(PARAM_SET)
				.withParameter(ContextParamTypes.THIS_ENTITY, entity)
				.withParameter(ContextParamTypes.BLOCK_STATE, state)
				.withParameter(ContextParamTypes.ORIGIN, pos.getCenter())
				.build();
		return GameContext.of(level, paramMap);
	}

	@Override
	protected @NotNull VoxelShape getCollisionShape(
			@NotNull BlockState state,
			@NotNull BlockGetter blockGetter,
			@NotNull BlockPos pos,
			@NotNull CollisionContext context
	) {
		if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null) {
			Level level = null;
			if (blockGetter instanceof Level) {
				level = (Level) blockGetter;
			}

			try {
				if (test(level, entityContext.getEntity(), state, pos)) {
					return Shapes.block();
				}
			} catch (InvalidContextParameterException e) {
				Barricade.LOG.error("Failed to test shape", e);
			}
		}
		return Shapes.empty();
	}

    @Override
    protected @NotNull VoxelShape getShape(
		@NotNull BlockState state,
	    @NotNull BlockGetter blockGetter,
	    @NotNull BlockPos pos,
	    @NotNull CollisionContext context
    ) {
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Player player) {
            Level level = null;
            if (blockGetter instanceof Level) {
                level = (Level) blockGetter;
            }

	        try {
		        boolean isOperator = player.canUseGameMasterBlocks();
		        if (isOperator && !test(level, entityContext.getEntity(), state, pos)) {
			        return super.getShape(state, blockGetter, pos, context);
		        } else if (isOperator) {
			        return super.getShape(state, blockGetter, pos, context);
		        }
	        } catch (InvalidContextParameterException e) {
		        Barricade.LOG.error("Failed to test shape", e);
	        }
        }
        return Shapes.empty();
    }
}

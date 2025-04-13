package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
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
import net.modgarden.silicate.api.SilicateRegistries;
import net.modgarden.silicate.api.condition.GameCondition;
import net.modgarden.silicate.api.context.GameContext;
import net.modgarden.silicate.api.context.param.ContextParamMap;
import net.modgarden.silicate.api.context.param.ContextParamSet;
import net.modgarden.silicate.api.context.param.ContextParamTypes;
import net.modgarden.silicate.api.exception.InvalidContextParameterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * A type of {@link BarrierBlock} that uses {@link GameCondition} to determine if an entity collides.
 */
public class PredicateBarrierBlock extends BarrierBlock {
	private static final MapCodec<PredicateBarrierBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			propertiesCodec(),
			ResourceLocation.CODEC
					.fieldOf("icon")
					.forGetter(PredicateBarrierBlock::icon),
			GameCondition.CODEC
					.fieldOf("conditions")
					.forGetter(PredicateBarrierBlock::rawCondition)
	).apply(instance, PredicateBarrierBlock::new));
	public static final ContextParamSet PARAM_SET = ContextParamSet.Builder
			.of()
			.required(ContextParamTypes.THIS_ENTITY)
			.required(ContextParamTypes.BLOCK_STATE)
			.required(ContextParamTypes.ORIGIN)
			.build();
	private final ResourceLocation icon;
	private final Function<RegistryAccess, Holder<GameCondition<?>>> function;
	private Holder<GameCondition<?>> condition;

	public PredicateBarrierBlock(Properties properties, ResourceLocation icon, ResourceKey<GameCondition<?>> conditionTemplate) {
		super(properties);
		this.icon = icon;
		this.function = registryAccess -> registryAccess.registryOrThrow(SilicateRegistries.CONDITION_TEMPLATE).getHolderOrThrow(conditionTemplate);
	}

	private PredicateBarrierBlock(Properties properties, ResourceLocation icon, Holder<GameCondition<?>> condition) {
		super(properties);
		this.icon = icon;
		this.function = null;
		this.condition = condition;
	}

	public ResourceLocation icon() {
		return icon;
	}

	public Holder<GameCondition<?>> condition(RegistryAccess registries) {
		if (condition == null && function != null)
			condition = function.apply(registries);
		return condition;
	}

	private Holder<GameCondition<?>> rawCondition() {
		return condition;
	}

	@Override
	public @NotNull MapCodec<BarrierBlock> codec() {
		return CODEC.xmap(
				properties -> properties,
				block -> (PredicateBarrierBlock) block
		);
	}

	@Override
	protected boolean skipRendering(BlockState state, BlockState adjacentState, @NotNull Direction direction) {
		return adjacentState.is(state.getBlock());
	}

	/**
	 * @return Whether to let the entity pass (true) or block the entity (false).
	 */
	public boolean test(
			@Nullable Level level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) throws InvalidContextParameterException {
		return !test(newContext(level, entity, state, pos));
	}

	public boolean test(GameContext context) {
		if (context.getLevel() == null)
			return rawCondition() != null && rawCondition().value().test(context);
		return this.condition(context.getLevel().registryAccess()).value().test(context);
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
					return Shapes.empty();
				}
			} catch (InvalidContextParameterException e) {
				Barricade.LOG.error("Failed to test shape", e);
			}
		}
		return Shapes.block();
	}

	@Override
	protected @NotNull VoxelShape getShape(
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
				boolean isOperator = entityContext.getEntity() instanceof Player player && player.getAbilities().instabuild;
				if (!isOperator && test(level, entityContext.getEntity(), state, pos)) {
					return Shapes.empty();
				}
			} catch (InvalidContextParameterException e) {
				Barricade.LOG.error("Failed to test shape", e);
			}
		}
		return super.getShape(state, blockGetter, pos, context);
	}
}

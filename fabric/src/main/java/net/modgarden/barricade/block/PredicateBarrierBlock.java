package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lgbt.greenhouse.silicate.api.context.parameter.GlobalParameterKeys;
import lgbt.greenhouse.silicate.api.context.parameter.ParameterMap;
import lgbt.greenhouse.silicate.api.predicate.GamePredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.data.BlockedDirections;
import lgbt.greenhouse.silicate.api.SilicateRegistries;
import lgbt.greenhouse.silicate.api.context.GameContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * A type of {@link BarrierBlock} that uses {@link GamePredicate} to determine if an entity collides.
 */
public class PredicateBarrierBlock extends StaticBarrierBlock {
	private static final MapCodec<PredicateBarrierBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			propertiesCodec(),
			Identifier.CODEC
					.fieldOf("icon")
					.forGetter(PredicateBarrierBlock::icon),
			GamePredicate.CODEC
					.fieldOf("conditions")
					.forGetter(PredicateBarrierBlock::rawCondition)
	).apply(instance, PredicateBarrierBlock::new));

	private final Identifier icon;
	private final Function<RegistryAccess, Holder<GamePredicate<?>>> function;
	private Holder<GamePredicate<?>> condition;

	public PredicateBarrierBlock(Properties properties, Identifier icon, ResourceKey<GamePredicate<?>> conditionTemplate) {
		super(properties, properties.blockIdOrThrow().identifier());
		this.icon = icon.withPrefix("barricade/icon/");
		this.function = registryAccess -> registryAccess.lookupOrThrow(SilicateRegistries.CONDITION).getOrThrow(conditionTemplate);
	}

	private PredicateBarrierBlock(Properties properties, Identifier icon, Holder<GamePredicate<?>> condition) {
		super(properties, properties.blockIdOrThrow().identifier());
		this.icon = icon.withPrefix("barricade/icon/");
		this.function = null;
		this.condition = condition;
	}

	public Identifier icon() {
		return icon;
	}

	public Holder<GamePredicate<?>> condition(RegistryAccess registries) {
		if (condition == null && function != null)
			condition = function.apply(registries);
		return condition;
	}

	private Holder<GamePredicate<?>> rawCondition() {
		return condition;
	}

	@Override
	public @NotNull MapCodec<BarrierBlock> codec() {
		return CODEC.xmap(
				properties -> properties,
				block -> (PredicateBarrierBlock) block
		);
	}

	/**
	 * @return Whether to let the entity pass (true) or block the entity (false).
	 */
	public boolean test(
			@NotNull Level level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) {
		return !test(newContext(level, entity, state, pos));
	}

	public boolean test(GameContext context) {
		if (context.getLevel() == null)
			return rawCondition() != null && rawCondition().value().test(context);
		return this.condition(context.getLevel().registryAccess()).value().test(context);
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
			} else {
				return Shapes.block();
			}

			try {
				if (test(level, entityContext.getEntity(), state, pos)) {
					return Shapes.empty();
				}
			} catch (Exception e) {
				BarricadeMod.LOG.error("Failed to test shape", e);
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
			} catch (Exception e) {
				BarricadeMod.LOG.error("Failed to test shape", e);
			}
		}
		return super.getShape(state, blockGetter, pos, context);
	}

	@Override
	public BlockedDirections getBlockedDirections() {
		return BlockedDirections.all();
	}

	@Override
	public Identifier getIcon() {
		return this.icon();
	}
}

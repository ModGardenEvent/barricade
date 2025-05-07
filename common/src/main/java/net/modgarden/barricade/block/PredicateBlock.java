package net.modgarden.barricade.block;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
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
 * <h2>How to make a PredicateBlock</h2>
 * <ol>
 *     <li>Extend the PredicateBlock class and set T to the simulated block.</li>
 *     <li>Create constructors as shown in PredicateLeverBlock.</li>
 *     <li>Override all methods that your block and its superclasses override, then call those methods using accessors.</li>
 *     <li>Set up a method called registerState and call the methods as shown in PredicateLeverBlock</li>
 * </ol>
 * @param <T> The type of the block to simulate.
 */
public abstract class PredicateBlock<T extends Block> extends Block {
	public static final ContextParamSet PARAM_SET = ContextParamSet.Builder
			.of()
			.required(ContextParamTypes.THIS_ENTITY)
			.required(ContextParamTypes.BLOCK_STATE)
			.required(ContextParamTypes.ORIGIN)
			.build();

	private final T simulated;
	private final ResourceLocation icon;
	private final Function<RegistryAccess, Holder<GameCondition<?>>> registryToCondition;
	private Holder<GameCondition<?>> condition;

	public PredicateBlock(T simulated, Properties properties, ResourceLocation icon, ResourceKey<GameCondition<?>> conditionTemplate) {
		super(properties);
		this.simulated = simulated;
		this.icon = icon;
		this.registryToCondition = registryAccess -> registryAccess.registryOrThrow(SilicateRegistries.CONDITION_TEMPLATE).getHolderOrThrow(conditionTemplate);
	}

	protected PredicateBlock(T simulated, Properties properties, ResourceLocation icon, Holder<GameCondition<?>> condition) {
		super(properties);
		this.simulated = simulated;
		this.icon = icon;
		this.registryToCondition = null;
		this.condition = condition;
	}

	public ResourceLocation icon() {
		return icon;
	}

	public Holder<GameCondition<?>> condition(RegistryAccess registries) {
		if (condition == null && registryToCondition != null)
			condition = registryToCondition.apply(registries);
		return condition;
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

	/**
	 * @return Whether to let the entity interact (true) or block the entity's interaction (false).
	 */
	public boolean test(
			@Nullable Level level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) throws InvalidContextParameterException {
		// Inverted for compatibility with the old Predicate Barrier Block system
		return !test(newContext(level, entity, state, pos));
	}

	public final boolean test(GameContext context) {
		if (context.getLevel() == null)
			return rawCondition() != null && rawCondition().value().test(context);
		return this.condition(context.getLevel().registryAccess()).value().test(context);
	}

	protected @NotNull MapCodec<PredicateBlock<T>> mapCodec(Constructor<T> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				BlockBehaviour.propertiesCodec(),
				ResourceLocation.CODEC
						.fieldOf("icon")
						.forGetter(PredicateBlock::icon),
				GameCondition.CODEC
						.fieldOf("condition")
						.forGetter(PredicateBlock::rawCondition)
		).apply(instance, constructor));
	}

	protected Holder<GameCondition<?>> rawCondition() {
		return condition;
	}

	protected T simulated() {
		return simulated;
	}

	protected boolean isShaped(
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
				if (!test(level, entityContext.getEntity(), state, pos)) {
					return false;
				}
			} catch (InvalidContextParameterException e) {
				Barricade.LOG.error("Failed to test shape", e);
			}
		}
		return true;
	}

	// super mario in real life
	@FunctionalInterface
	public interface Constructor<T extends Block> extends Function3<Properties, ResourceLocation, Holder<GameCondition<?>>, PredicateBlock<T>> {
		@Override
		PredicateBlock<T> apply(Properties properties, ResourceLocation icon, Holder<GameCondition<?>> condition);
	}
}

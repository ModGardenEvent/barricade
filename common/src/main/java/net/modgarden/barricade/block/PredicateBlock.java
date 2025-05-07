package net.modgarden.barricade.block;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
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
import net.modgarden.barricade.mixin.BlockBehaviourAccessor;
import net.modgarden.barricade.mixin.self.PredicateBlocksMixin;
import net.modgarden.silicate.api.condition.GameCondition;
import net.modgarden.silicate.api.context.GameContext;
import net.modgarden.silicate.api.context.param.ContextParamMap;
import net.modgarden.silicate.api.context.param.ContextParamSet;
import net.modgarden.silicate.api.context.param.ContextParamTypes;
import net.modgarden.silicate.api.exception.InvalidContextParameterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.modgarden.barricade.Barricade.MIXIN_NOT_APPLIED;

/**
 * <h2>How to make a PredicateBlock</h2>
 * <ol>
 *     <li>Create a class extending the target block and implementing {@link PredicateBlock}.</li>
 *     <li>Create dummy constructors as shown in {@link PredicateLeverBlock}.</li>
 *     <li>Override the {@code codec} method and return a call to {@link PredicateBlock#mapCodec(Constructor)}, passing a reference to the second constructor.</li>
 *     <li>Add the class to the mixin targets in {@link PredicateBlocksMixin}.</li>
 *     <li>Override the server-authoritative methods and the client-side behavior methods.</li>
 *     <li>Add your logic by calling methods in {@link PredicateBlock}.</li>
 * </ol>
 */
public interface PredicateBlock {
	ContextParamSet PARAM_SET = ContextParamSet.Builder
			.of()
			.required(ContextParamTypes.THIS_ENTITY)
			.required(ContextParamTypes.BLOCK_STATE)
			.required(ContextParamTypes.ORIGIN)
			.build();

	static GameContext newContext(
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

	static @NotNull <S extends Block, T extends Block & PredicateBlock> MapCodec<S> mapCodec(PredicateBlock.Constructor<T> constructor) {
		return RecordCodecBuilder.<T>mapCodec(instance -> instance.group(
				BlockBehaviourAccessor.invokePropertiesCodec(),
				ResourceLocation.CODEC
						.fieldOf("icon")
						.forGetter(PredicateBlock::barricade$icon),
				GameCondition.CODEC
						.fieldOf("condition")
						.forGetter(PredicateBlock::barricade$rawCondition)
		).apply(instance, constructor))
				.xmap(
						t -> {
							// This is a downcast, so it's always safe.
							//noinspection unchecked
							return (S) t;
						},
						s -> {
							// This upcast is always safe because the object being deserialized
							// would be of type T if using this codec.
							//noinspection unchecked
							return (T) s;
						}
				);
	}

	default ResourceLocation barricade$icon() {
		throw new IllegalStateException(MIXIN_NOT_APPLIED);
	}

	default Holder<GameCondition<?>> barricade$condition(RegistryAccess registries) {
		throw new IllegalStateException(MIXIN_NOT_APPLIED);
	}

	default @Nullable Holder<GameCondition<?>> barricade$rawCondition() {
		throw new IllegalStateException(MIXIN_NOT_APPLIED);
	}

	/**
	 * @return Whether to let the entity interact (true) or block the entity's interaction (false).
	 */
	default boolean barricade$test(
			@Nullable Level level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) throws InvalidContextParameterException {
		throw new IllegalStateException(MIXIN_NOT_APPLIED);
	}

	default boolean barricade$test(GameContext context) {
		throw new IllegalStateException(MIXIN_NOT_APPLIED);
	}

	default boolean isShaped(
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
				if (!barricade$test(level, entityContext.getEntity(), state, pos)) {
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
	interface Constructor<T extends Block & PredicateBlock> extends Function3<BlockBehaviour.Properties, ResourceLocation, Holder<GameCondition<?>>, T> {
		@Override
		T apply(BlockBehaviour.Properties properties, ResourceLocation icon, Holder<GameCondition<?>> condition);
	}
}

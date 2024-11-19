package net.modgarden.barricade.block;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.AgnosticLootContext$Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A type of {@link BarrierBlock} that uses {@link LootItemCondition} to determine if an entity collides.
 */
public class PredicateBarrierBlock extends BarrierBlock {
	private static final MapCodec<PredicateBarrierBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			propertiesCodec(),
			ResourceLocation.CODEC
					.fieldOf("icon")
					.forGetter(PredicateBarrierBlock::icon),
			LootItemCondition.DIRECT_CODEC.listOf()
					.fieldOf("conditions")
					.validate(conditions -> {
						try {
							conditions.forEach(PredicateBarrierBlock::validateCondition);
							return DataResult.success(conditions);
						} catch (IllegalArgumentException e) {
							return DataResult.error(e::getMessage);
						}
					})
					.forGetter(PredicateBarrierBlock::conditions)
	).apply(instance, PredicateBarrierBlock::new));
	public static final LootContextParamSet PARAM_SET = LootContextParamSet.builder()
			.required(LootContextParams.THIS_ENTITY)
			.required(LootContextParams.BLOCK_STATE)
			.required(LootContextParams.ORIGIN)
			.build();
	private final ResourceLocation icon;
	private final List<LootItemCondition> conditions;

	/**
	 * @throws IllegalArgumentException if the conditions are invalid for this {@link LootContextParamSet}.
	 */
	public PredicateBarrierBlock(Properties properties, ResourceLocation icon, LootItemCondition... conditions) throws IllegalArgumentException {
		super(properties);
		this.icon = icon;
		this.conditions = List.of(conditions);
		this.conditions.forEach(PredicateBarrierBlock::validateCondition);
	}
	
	private PredicateBarrierBlock(Properties properties, ResourceLocation icon,  List<LootItemCondition> conditions) {
		super(properties);
		this.icon = icon;
		this.conditions = conditions;
	}

	public ResourceLocation icon() {
		return icon;
	}
	
	public List<LootItemCondition> conditions() {
		return conditions;
	}
	
	@Override
	public @NotNull MapCodec<BarrierBlock> codec() {
		return CODEC.xmap(
				properties -> properties,
				block -> (PredicateBarrierBlock) block
		);
	}
	
	public boolean test(
			@Nullable ServerLevel level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) {
		return test(newContext(level, entity, state, pos));
	}
	
	public boolean test(LootContext context) {
		return this.conditions().stream().allMatch(condition -> condition.test(context));
	}
	
	@SuppressWarnings("DataFlowIssue") // allow client-side checks
	public static LootContext newContext(
			@Nullable ServerLevel level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) {
		return ((AgnosticLootContext$Builder) new LootContext.Builder(
				new LootParams.Builder(level)
						.withParameter(LootContextParams.THIS_ENTITY, entity)
						.withParameter(LootContextParams.BLOCK_STATE, state)
						.withParameter(LootContextParams.ORIGIN, pos.getCenter())
						.create(PARAM_SET)
		))
				.barricade$create();
	}

	@Override
	protected @NotNull VoxelShape getCollisionShape(
			@NotNull BlockState state,
			@NotNull BlockGetter level,
			@NotNull BlockPos pos,
			@NotNull CollisionContext context
	) {
		if (context instanceof EntityCollisionContext entityContext) {
			ServerLevel serverLevel = null;
			if (level instanceof ServerLevel) {
				serverLevel = (ServerLevel) level;
			}
			
			if (entityContext.getEntity() != null && !test(serverLevel, entityContext.getEntity(), state, pos))
				return Shapes.empty();
		}
		return super.getCollisionShape(state, level, pos, context);
	}

	private static void validateCondition(LootItemCondition condition) throws IllegalArgumentException {
		var problemReporter = new ProblemReporter.Collector();
		condition.validate(
				new ValidationContext(problemReporter, PARAM_SET)
		);
		problemReporter.getReport().ifPresent(message -> {
			throw new IllegalArgumentException("Failed to validate PredicateBarrierBlock conditions: " + message);
		});
	}
}

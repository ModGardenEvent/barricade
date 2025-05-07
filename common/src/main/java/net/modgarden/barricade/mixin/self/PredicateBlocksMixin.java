package net.modgarden.barricade.mixin.self;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.block.PredicateBlock;
import net.modgarden.barricade.block.PredicateLeverBlock;
import net.modgarden.silicate.api.SilicateRegistries;
import net.modgarden.silicate.api.condition.GameCondition;
import net.modgarden.silicate.api.context.GameContext;
import net.modgarden.silicate.api.exception.InvalidContextParameterException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin({PredicateLeverBlock.class})
public class PredicateBlocksMixin implements PredicateBlock {
	@Unique
	private ResourceLocation barricade$icon;
	@Unique
	private Function<RegistryAccess, Holder<GameCondition<?>>> barricade$registryToCondition;
	@Unique
	private Holder<GameCondition<?>> barricade$condition;

	@Inject(
			method = "<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceKey;)V",
			at = @At("CTOR_HEAD")
	)
	private void init(BlockBehaviour.Properties properties, ResourceLocation icon, ResourceKey<GameCondition<?>> conditionTemplate, CallbackInfo ci) {
		this.barricade$icon = icon;
		this.barricade$registryToCondition = registryAccess -> registryAccess.registryOrThrow(SilicateRegistries.CONDITION_TEMPLATE).getHolderOrThrow(conditionTemplate);
	}

	@Inject(
			method = "<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/core/Holder;)V",
			at = @At("CTOR_HEAD")
	)
	private void init(BlockBehaviour.Properties properties, ResourceLocation icon, Holder<GameCondition<?>> condition, CallbackInfo ci) {
		this.barricade$icon = icon;
		this.barricade$registryToCondition = null;
		this.barricade$condition = condition;
	}

	@Override
	public ResourceLocation barricade$icon() {
		return barricade$icon;
	}

	@Override
	public Holder<GameCondition<?>> barricade$condition(RegistryAccess registries) {
		if (barricade$condition == null && barricade$registryToCondition != null)
			barricade$condition = barricade$registryToCondition.apply(registries);
		return barricade$condition;
	}

	@Override
	public boolean barricade$test(
			@Nullable Level level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) throws InvalidContextParameterException {
		// Inverted for compatibility with the old Predicate Barrier Block system
		return !barricade$test(PredicateBlock.newContext(level, entity, state, pos));
	}

	@Override
	public final boolean barricade$test(GameContext context) {
		if (context.getLevel() == null)
			return barricade$rawCondition() != null && barricade$rawCondition().value().test(context);
		return this.barricade$condition(context.getLevel().registryAccess()).value().test(context);
	}

	@Override
	public Holder<GameCondition<?>> barricade$rawCondition() {
		return barricade$condition;
	}
}

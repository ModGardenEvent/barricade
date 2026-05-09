package net.modgarden.barricade.mixin;

import lgbt.greenhouse.silicate.api.predicate.GamePredicate;
import lgbt.greenhouse.silicate.api.predicate.meta.DynamicMetaPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DynamicMetaPredicate.class)
public interface Accessor_DynamicMetaPredicate<T extends GamePredicate<T>> {
	@Accessor("predicate")
	GamePredicate<T> barricade$getPredicate();
}

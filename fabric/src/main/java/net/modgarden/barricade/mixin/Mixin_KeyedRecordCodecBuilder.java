package net.modgarden.barricade.mixin;

import java.util.function.Function;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import lgbt.greenhouse.silicate.api.predicate.meta.DynamicMetaPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.lukebemish.codecextras.record.KeyedRecordCodecBuilder$1")
public abstract class Mixin_KeyedRecordCodecBuilder<A> {
	@WrapOperation(method = "encodePartial", at = @At(value = "INVOKE", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"))
	private <T, R> R unwrapDynamicMetaPredicate(
			Function<T, R> instance,
			T t,
			Operation<R> original
	) {
		if (t instanceof DynamicMetaPredicate<?> dynamicMetaPredicate) {
			return original.call(instance, ((Accessor_DynamicMetaPredicate<?>) (Object) dynamicMetaPredicate).barricade$getPredicate());
		}

		return original.call(instance, t);
	}
}

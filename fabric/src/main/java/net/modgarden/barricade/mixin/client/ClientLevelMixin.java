package net.modgarden.barricade.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(ClientLevel.class)
public final class ClientLevelMixin {
	@WrapOperation(
			method = "getMarkerParticleTarget",
			at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z")
	)
	private boolean noBarrierParticle(Set<Item> instance, Object o, Operation<Boolean> original) {
		return !((Item) o).getDefaultInstance().is(Items.BARRIER) && original.call(instance, o);
	}
}

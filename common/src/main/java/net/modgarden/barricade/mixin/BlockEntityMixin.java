package net.modgarden.barricade.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.registry.BarricadeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
	@WrapWithCondition(
			method = "saveWithoutMetadata",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V")
	)
	private boolean cancelDataSer(BlockEntity instance, CompoundTag tag, HolderLookup.Provider registries) {
		if ((BlockEntity) (Object) this instanceof AdvancedBarrierBlockEntity barrier) {
			if (barrier.getHolder().unwrapKey().isEmpty() || !barrier.hasLevel()) return true;
			assert barrier.getLevel() != null;
			Registry<AdvancedBarrier> registry = barrier.getLevel().registryAccess().registryOrThrow(BarricadeRegistries.ADVANCED_BARRIER);
			tag.putInt("a", registry.getId(barrier.getData()));
			return false;
		} else return true;
	}
}

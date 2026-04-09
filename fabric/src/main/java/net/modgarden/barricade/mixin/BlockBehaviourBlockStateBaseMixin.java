package net.modgarden.barricade.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.BarricadeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviourBlockStateBaseMixin {

	@Shadow
	protected abstract BlockState asState();

	@ModifyReturnValue(method = "getRenderShape", at = @At("RETURN"))
	private RenderShape setRenderShapeDependingOnModel(RenderShape original) {
		if (BarricadeMod.isOperatorModel(asState())) {
			return RenderShape.MODEL;
		}

		return original;
	}

	@ModifyReturnValue(method = "shouldSpawnTerrainParticles", at = @At("RETURN"))
	private boolean setRenderShapeDependingOnModel(boolean original) {
		if (BarricadeMod.isOperatorModel(asState())) {
			return false;
		}

		return original;
	}
}

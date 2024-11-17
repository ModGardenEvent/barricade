package net.modgarden.barricade.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.Barricade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviourBlockStateBaseMixin {

    @Shadow protected abstract BlockState asState();

    @ModifyReturnValue(method = "getRenderShape", at = @At("RETURN"))
    private RenderShape barricade$setRenderShapeDependingOnModel(RenderShape original) {
        if (Barricade.isOperatorModel(asState().getBlock()))
            return RenderShape.MODEL;
        return original;
    }
}

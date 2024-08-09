package net.modgarden.barricade.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.RenderShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BarrierBlock.class, priority = 500)
public class BarrierMixin {
    @ModifyReturnValue(method = "getRenderShape", at = @At("RETURN"))
    private RenderShape barricade$barrierBlockModel(RenderShape original) {
        return RenderShape.MODEL;
    }
}

package net.modgarden.barricade.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    @ModifyReturnValue(method = "skipRendering", at = @At("RETURN"))
    private boolean barricade$skipRenderingFaces(boolean original, BlockState state, BlockState adjacentState, Direction direction) {
        if (((BlockBehaviour)(Object)this) instanceof Block block && block instanceof BarrierBlock) {
            return adjacentState.is(state.getBlock());
        }
        return original;
    }
}

package net.modgarden.barricade.fabric.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;
import net.modgarden.barricade.registry.BarricadeBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public class BlockMixin {
    @ModifyExpressionValue(method = "shouldRenderFace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;skipRendering(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z"))
    private static boolean barricade$shouldRenderAdvancedBarrierFace(boolean original, BlockState state, BlockGetter level, BlockPos offset, Direction face, BlockPos pos) {
        if (state.is(BarricadeBlocks.ADVANCED_BARRIER))
            return level.getBlockState(offset).is(state.getBlock()) || (!(level.getBlockEntity(pos) instanceof AdvancedBarrierBlockEntity blockEntity) || blockEntity.getBlockedDirections() == null || !blockEntity.getBlockedDirections().blocks(face));
        return original;
    }
}

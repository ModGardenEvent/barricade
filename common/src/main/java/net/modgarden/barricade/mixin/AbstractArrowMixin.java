package net.modgarden.barricade.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.block.AdvancedBarrierBlock;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.block.PredicateBarrierBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @ModifyVariable(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape barricade$entityCollision(VoxelShape shape, @Local BlockPos pos, @Local BlockState state) {
        Block block = state.getBlock();
        if (block instanceof AdvancedBarrierBlock || block instanceof DirectionalBarrierBlock || block instanceof PredicateBarrierBlock)
            return state.getCollisionShape(((AbstractArrow)(Object)this).level(), pos, CollisionContext.of((AbstractArrow)(Object)this));
        return shape;
    }
}

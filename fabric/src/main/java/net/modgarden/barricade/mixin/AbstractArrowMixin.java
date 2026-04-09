package net.modgarden.barricade.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
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
	@Definition(id = "getCollisionShape", method = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;")
	@Expression("? = ?.getCollisionShape(?, ?)")
	@ModifyVariable(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER), name = "shape")
	private VoxelShape entityCollision(VoxelShape shape, @Local BlockPos pos, @Local BlockState state) {
		Block block = state.getBlock();
		if (block instanceof AdvancedBarrierBlock || block instanceof DirectionalBarrierBlock || block instanceof PredicateBarrierBlock)
			// IntelliJ doesn't like that we're getting this
			//noinspection DataFlowIssue
			return state.getCollisionShape(((AbstractArrow) (Object) this).level(), pos, CollisionContext.of((AbstractArrow) (Object) this));
		return shape;
	}
}

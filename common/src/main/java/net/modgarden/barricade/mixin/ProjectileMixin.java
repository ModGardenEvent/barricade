package net.modgarden.barricade.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.modgarden.barricade.block.AdvancedBarrierBlock;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.block.PredicateBarrierBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {
    public ProjectileMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(method = "onHit", at = @At(value = "FIELD", target = "Lnet/minecraft/world/phys/HitResult$Type;BLOCK:Lnet/minecraft/world/phys/HitResult$Type;"))
    private HitResult.Type barricade$dontAlwaysCountBarriersAsHit(HitResult.Type original, HitResult result) {
        if (original != HitResult.Type.BLOCK)
            return original;

        BlockPos pos = ((BlockHitResult)result).getBlockPos();
        BlockState state = level().getBlockState(pos);
        Block block = state.getBlock();

        // FIXME: This could probably be cleaner. Maybe an interface to mark barricade barriers?
        if (!(block instanceof AdvancedBarrierBlock) || !(block instanceof DirectionalBarrierBlock) && !(block instanceof PredicateBarrierBlock))
            return original;

        return state.getCollisionShape(level(), pos, CollisionContext.of(this)) == Shapes.block() ? HitResult.Type.BLOCK : HitResult.Type.MISS;
    }
}

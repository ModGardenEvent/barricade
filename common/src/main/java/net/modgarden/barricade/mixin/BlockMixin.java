package net.modgarden.barricade.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.registry.BarricadeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Run before other mixins that modify this.
@Mixin(value = Block.class, priority = 500)
public class BlockMixin {
    @ModifyReturnValue(method = "isExceptionForConnection", at = @At("RETURN"))
    private static boolean barricade$exceptionForConnection(boolean original, BlockState state) {
        return original || state.is(BarricadeTags.BlockTags.BARRIERS);
    }
}

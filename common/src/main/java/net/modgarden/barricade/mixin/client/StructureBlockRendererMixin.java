package net.modgarden.barricade.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.blockentity.StructureBlockRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.registry.BarricadeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StructureBlockRenderer.class)
public class StructureBlockRendererMixin {
    @ModifyVariable(method = "renderInvisibleBlocks", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", ordinal = 2), ordinal = 2)
    private boolean barrier$allowOtherBarriers(boolean value, @Local BlockState state) {
        if (state.is(BarricadeTags.BlockTags.BARRIERS))
            return true;
        return value;
    }
}

package net.modgarden.barricade.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.RenderShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LightBlock.class, priority = 500)
public class LightBlockMixin {
    @ModifyReturnValue(method = "getRenderShape", at = @At("RETURN"))
    private RenderShape barricade$barrierBlockModel(RenderShape original) {
        var player = Minecraft.getInstance().player;

        if (player == null || !player.canUseGameMasterBlocks() || !player.getMainHandItem().is(Items.LIGHT)) {
            return original;
        }

        return RenderShape.MODEL;
    }
}

package net.modgarden.barricade.mixin.client;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface LevelRendererInvoker {
    @Invoker("setBlockDirty")
    void barricade$invokeSetBlockDirty(BlockPos pos, boolean reRenderOnMainThread);
}

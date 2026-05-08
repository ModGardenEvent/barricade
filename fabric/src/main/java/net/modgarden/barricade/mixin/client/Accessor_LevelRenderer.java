package net.modgarden.barricade.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;

@Mixin(LevelRenderer.class)
public interface Accessor_LevelRenderer {
	@Invoker("setBlockDirty")
	void barricade$setBlockDirty(BlockPos pos, boolean playerChanged);
}

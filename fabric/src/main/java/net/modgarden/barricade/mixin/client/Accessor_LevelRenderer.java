package net.modgarden.barricade.mixin.client;

import com.mojang.blaze3d.textures.GpuSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.LevelRenderer;

@Mixin(LevelRenderer.class)
public interface Accessor_LevelRenderer {
	@Accessor("chunkLayerSampler")
	GpuSampler barricade$getChunkLayerSampler();
}

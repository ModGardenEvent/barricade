package net.modgarden.barricade.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;

@Mixin(RenderSectionRegion.class)
public interface Accessor_RenderSectionRegion {
	@Accessor("level")
	ClientLevel barricade$getLevel();
}

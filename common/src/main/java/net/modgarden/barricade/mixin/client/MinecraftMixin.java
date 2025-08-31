package net.modgarden.barricade.mixin.client;

import net.minecraft.client.Minecraft;
import net.modgarden.barricade.client.renderer.block.AdvancedBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.block.BakedBarrierBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "onGameLoadFinished", at = @At("RETURN"))
	private void barricade$resetAdvancedBarrierModels(Minecraft.GameLoadCookie gameLoadCookie, CallbackInfo ci) {
		AdvancedBarrierBlockRenderer.reloadModels();
		BakedBarrierBlockRenderer.reloadModels();
	}
}

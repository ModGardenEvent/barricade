package net.modgarden.barricade.mixin.client;

import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.render.BarricadeRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;

@Mixin(PlayerInfo.class)
public abstract class Mixin_PlayerInfo {
	@Inject(method = "setGameMode", at = @At("RETURN"))
	public void onSetGameMode(
			GameType gameMode,
			CallbackInfo ci
	) {
		if (!BarricadeClient.CONFIG.get().everythingVisible()) {
			BarricadeRendering.reloadBarriers();
		}
	}
}

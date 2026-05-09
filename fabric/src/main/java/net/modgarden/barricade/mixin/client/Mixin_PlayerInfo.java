package net.modgarden.barricade.mixin.client;

import com.mojang.authlib.GameProfile;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.BarricadeClientConfig;
import net.modgarden.barricade.client.render.BarricadeRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;

@Mixin(PlayerInfo.class)
public abstract class Mixin_PlayerInfo {
	@Shadow
	public abstract GameProfile getProfile();

	@Inject(method = "setGameMode", at = @At("RETURN"))
	public void onSetGameMode(
			GameType gameMode,
			CallbackInfo ci
	) {
		if (!this.getProfile().equals(Minecraft.getInstance().getGameProfile())) return;

		BarricadeClientConfig config = BarricadeClient.CONFIG.get();

		if (config.disableInSurvival()) {
			BarricadeRendering.reloadBarriers();
		}
	}
}

package net.modgarden.barricade.mixin.client;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.modgarden.barricade.BarricadeMod;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(TextureAtlas.class)
public class TextureAtlasMixin {
	@Shadow
	private @Nullable TextureAtlasSprite missingSprite;

	@Shadow
	private Map<Identifier, TextureAtlasSprite> texturesByName;

	@Inject(
			method = "getSprite",
			at = @At("TAIL"),
			cancellable = true
	)
	private void warnMissingIcon(Identifier location, CallbackInfoReturnable<TextureAtlasSprite> cir) {
		TextureAtlasSprite sprite = cir.getReturnValue();
		// FIXME LATER: why the hell was this ever here? who in their right mind would do it this way? holy shit.
		// In case we encounter a double prefix
		if (sprite != null && sprite.equals(this.missingSprite) && location.getPath().startsWith("barricade/icon/barricade/icon/")) {
			Identifier newName = location.withPath(location.getPath().replaceFirst("barricade/icon/", ""));
			cir.setReturnValue(this.texturesByName.getOrDefault(newName, this.missingSprite));
			sprite = cir.getReturnValue();
		}
		if (sprite != null && sprite.equals(this.missingSprite) && location.getPath().startsWith("barricade/icon/")) {
			// Warn user if icon is null
			BarricadeMod.LOG.warn("Icon is missingno. \"{}\"", location);
		}
	}
}

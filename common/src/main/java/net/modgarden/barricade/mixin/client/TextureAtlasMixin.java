package net.modgarden.barricade.mixin.client;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.modgarden.barricade.Barricade;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(TextureAtlas.class)
public class TextureAtlasMixin {
	@Shadow private @Nullable TextureAtlasSprite missingSprite;

	@Shadow private Map<ResourceLocation, TextureAtlasSprite> texturesByName;

	@Inject(
			method = "getSprite",
			at = @At("TAIL"),
			cancellable = true
	)
	private void warnMissingIcon(ResourceLocation name, CallbackInfoReturnable<TextureAtlasSprite> cir) {
		TextureAtlasSprite sprite = cir.getReturnValue();
		// In case we encounter a double prefix
		if (sprite != null && sprite.equals(this.missingSprite) && name.getPath().startsWith("barricade/icon/barricade/icon/")) {
			ResourceLocation newName = name.withPath(name.getPath().replaceFirst("barricade/icon/", ""));
			cir.setReturnValue(this.texturesByName.getOrDefault(newName, this.missingSprite));
			sprite = cir.getReturnValue();
		}
		if (sprite != null && sprite.equals(this.missingSprite) && name.getPath().startsWith("barricade/icon/")) {
			// Warn user if icon is null
			Barricade.LOG.warn("Icon is missingno. \"{}\"", name);
		}
	}
}

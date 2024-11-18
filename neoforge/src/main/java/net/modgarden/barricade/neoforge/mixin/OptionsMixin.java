package net.modgarden.barricade.neoforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Options.class)
public class OptionsMixin {
    @ModifyReturnValue(method = "loadSelectedResourcePacks", at = @At("RETURN"))
    private void barricade$enableModdedRenderingIfFirstTime() {

    }
}

package net.modgarden.barricade.mixin.self;

import net.modgarden.barricade.Barricade;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Hacks for hotswapping.
 */
@Mixin(Barricade.class)
public class BarricadeMixin {
	/**
	 * Fixes an {@link IllegalAccessError} when hotswapping.
	 */
	@Mutable
	@Final
	@Shadow
	public static Logger LOG;
}

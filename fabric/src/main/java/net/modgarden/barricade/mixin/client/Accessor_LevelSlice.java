package net.modgarden.barricade.mixin.client;

import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.multiplayer.ClientLevel;

@Mixin(LevelSlice.class)
public interface Accessor_LevelSlice {
	@Accessor("level")
	ClientLevel barricade$getLevel();
}

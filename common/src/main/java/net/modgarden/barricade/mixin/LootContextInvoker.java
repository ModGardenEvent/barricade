package net.modgarden.barricade.mixin;

import net.minecraft.core.HolderGetter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootContext.class)
public interface LootContextInvoker {
	@Invoker("<init>")
	static LootContext ctor(LootParams params, RandomSource random, HolderGetter.Provider lootDataResolver) {
		throw new UnsupportedOperationException();
	}
}

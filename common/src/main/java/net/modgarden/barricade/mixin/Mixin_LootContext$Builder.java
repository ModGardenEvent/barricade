package net.modgarden.barricade.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.modgarden.barricade.AgnosticLootContext$Builder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LootContext.Builder.class)
public class Mixin_LootContext$Builder implements AgnosticLootContext$Builder {
	@Shadow @Final private LootParams params;

	@Override
	public LootContext barricade$create() {
		return LootContextInvoker.ctor(this.params, RandomSource.create(), null);
	}
}

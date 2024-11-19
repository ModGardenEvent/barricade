package net.modgarden.barricade.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.modgarden.barricade.AgnosticLootContext$Builder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(LootContext.Builder.class)
public abstract class LootContextBuilderMixin implements AgnosticLootContext$Builder {
	@Shadow @Final private LootParams params;

    @Shadow public abstract ServerLevel getLevel();

    @Shadow public abstract LootContext create(Optional<ResourceLocation> sequence);

    @Override
	public LootContext barricade$create() {
        if (getLevel() == null)
            return LootContextInvoker.ctor(this.params, RandomSource.create(), null);
        return create(Optional.empty());
	}
}

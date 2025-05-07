package net.modgarden.barricade.mixin;

import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.block.PredicateBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PredicateBlock.class)
public interface PredicateBlockAccessor {
	@Accessor
	Block getSimulated();
}

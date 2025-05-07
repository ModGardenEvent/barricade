package net.modgarden.barricade.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface BlockAccessor {
	@Accessor
	void setDefaultBlockState(BlockState state);

	@Mutable
	@Accessor
	void setBuiltInRegistryHolder(Holder.Reference<Block> reference);

	@Accessor
	Holder.Reference<Block> getBuiltInRegistryHolder();
}

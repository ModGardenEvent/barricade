package net.modgarden.barricade.datagen.mixin;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TextureMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import static net.modgarden.barricade.Barricade.MIXIN_NOT_APPLIED;

@Mixin(BlockModelGenerators.BlockFamilyProvider.class)
public interface BlockFamilyProviderAccessor {
	@Invoker("<init>")
	static BlockModelGenerators.BlockFamilyProvider init(final BlockModelGenerators this$0, final TextureMapping mapping) {
		throw new IllegalStateException(MIXIN_NOT_APPLIED);
	}
}

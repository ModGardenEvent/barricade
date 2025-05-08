package net.modgarden.barricade.datagen.mixin;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockModelGenerators.class)
public interface BlockModelGeneratorsAccessor {
	@Accessor
	Map<Block, TexturedModel> getTexturedModels();
}

package net.modgarden.barricade.client.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.client.util.OperatorBlockPseudoTag;

public interface OperatorBakedModelAccess {
	Either<OperatorBlockPseudoTag, ResourceKey<Block>> requiredBlock();
}

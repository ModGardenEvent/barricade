package net.modgarden.barricade.client.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface CreativeOnlyBakedModelAccess {
    Either<TagKey<Block>, ResourceKey<Block>> tagOrBlock();
}

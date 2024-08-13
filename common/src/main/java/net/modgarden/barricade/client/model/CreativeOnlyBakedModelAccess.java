package net.modgarden.barricade.client.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface CreativeOnlyBakedModelAccess {
    Either<TagKey<Item>, ResourceKey<Item>> requiredItem();
}

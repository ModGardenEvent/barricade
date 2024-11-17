package net.modgarden.barricade.client.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.modgarden.barricade.client.util.OperatorItemPseudoTag;

public interface OperatorBakedModelAccess {
    Either<OperatorItemPseudoTag, ResourceKey<Item>> requiredItem();
}

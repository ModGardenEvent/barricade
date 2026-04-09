package net.modgarden.barricade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

@Mixin(TagsProvider.class)
public interface Accessor_TagsProvider {
	@Invoker("getOrCreateRawBuilder")
	<V> TagBuilder barricade$getOrCreateRawBuilder(TagKey<V> tagKey);
}

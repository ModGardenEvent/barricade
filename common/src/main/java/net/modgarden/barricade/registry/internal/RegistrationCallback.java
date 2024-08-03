package net.modgarden.barricade.registry.internal;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface RegistrationCallback<T> {
    void register(Registry<T> registry, ResourceLocation id, T object);
}
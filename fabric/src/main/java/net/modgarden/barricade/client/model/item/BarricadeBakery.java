package net.modgarden.barricade.client.model.item;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;

class BarricadeBakery {
	final Map<Identifier, Material.Baked> baked;
	private final MaterialBaker materials;

	public BarricadeBakery(MaterialBaker materials) {
		this.materials = materials;
		baked = new HashMap<>();
	}

	public static Material.@NonNull Baked getBarrier(
			Identifier identifier,
			MaterialBaker materials
	) {
		return materials.get(new Material(identifier), identifier::toString);
	}

	public Material.@NotNull Baked getOrBake(Identifier identifier) {
		return baked.computeIfAbsent(identifier, key -> getBarrier(key, materials));
	}
}

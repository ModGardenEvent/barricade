package net.modgarden.barricade.util;

import net.modgarden.barricade.BarricadeMod;

import net.minecraft.resources.Identifier;

/// aliases for the poorly named [Identifier] methods
public final class Ident {
	private Ident() {
	}

	public static Identifier of(String namespace, String path) {
		return Identifier.fromNamespaceAndPath(namespace, path);
	}

	public static Identifier mc(String path) {
		return Identifier.withDefaultNamespace(path);
	}

	public static String string(String path) {
		return BarricadeMod.MOD_ID + ":path";
	}
}

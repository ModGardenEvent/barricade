/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.modgarden.barricade.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * From <a href="https://github.com/Sweet-Berry-Collective/MoreThanAFoxbox/blob/46d26a81df5099788338604cb0709b8fdda95334/src/main/java/dev/sweetberry/more_than_a_foxbox/registry/RegistryContext.java">More Than A Foxbox</a>.
 */
public class RegistryContext<TValue> {
	public final Registry<TValue> registry;
	public final String namespace;

	private final List<Value<TValue>> values = new ArrayList<>();

	public RegistryContext(Registry<TValue> registry, String namespace) {
		this.registry = registry;
		this.namespace = namespace;
	}

	@SuppressWarnings("unchecked")
	// Value<TValue> is equivalent to Value<T extends TValue>
	// Registry<TValue> is equivalent to Registry<T extends TValue>
	public <T extends TValue> Value<T> defer(String path, Function<ResourceKey<T>, T> createCallback) {
		var value = new Value<>(
				(Registry<T>) registry,
				Identifier.fromNamespaceAndPath(
						namespace,
						path
				),
				createCallback
		);

		values.add((Value<TValue>) value);

		return value;
	}

	public void register() {
		for (var value : values)
			value.get();
	}

	public static class Value<TValue> implements Supplier<TValue> {
		public final Registry<TValue> registry;
		public final Identifier location;
		public final Function<ResourceKey<TValue>, TValue> createCallback;

		private @Nullable TValue value = null;

		public Value(Registry<TValue> registry, Identifier location, Function<ResourceKey<TValue>, TValue> createCallback) {
			this.registry = registry;
			this.location = location;
			this.createCallback = createCallback;
		}

		@Override
		public @NotNull TValue get() {
			if (value == null)
				value = Registry.register(registry, location, createCallback.apply(ResourceKey.create(registry.key(), location)));

			return value;
		}
	}
}

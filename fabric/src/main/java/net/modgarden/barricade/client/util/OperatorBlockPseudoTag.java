package net.modgarden.barricade.client.util;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.BarricadeMod;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record OperatorBlockPseudoTag(HolderSet<Block> blocks, boolean replace) {
	public static final Codec<OperatorBlockPseudoTag> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
			RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("values").forGetter(OperatorBlockPseudoTag::blocks),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(OperatorBlockPseudoTag::replace)
	).apply(inst, OperatorBlockPseudoTag::new));
	public static final Codec<OperatorBlockPseudoTag> CODEC = Identifier.CODEC.xmap(
			Registry::get,
			operatorItemPseudoTag -> Registry.REGISTRY.entrySet()
					.stream()
					.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey))
					.get(operatorItemPseudoTag)
	);


	public static final OperatorBlockPseudoTag EMPTY = new OperatorBlockPseudoTag(HolderSet.empty(), false);

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof OperatorBlockPseudoTag tag))
			return false;
		return tag.blocks.equals(blocks);
	}

	@Override
	public int hashCode() {
		return Objects.hash(blocks);
	}

	public OperatorBlockPseudoTag combine(OperatorBlockPseudoTag other) {
		var set = HolderSet.direct(other.blocks.stream().toList());
		if (!other.replace)
			set = HolderSet.direct(Stream.concat(other.blocks.stream(), blocks.stream()).toList());
		return new OperatorBlockPseudoTag(set, other.replace);
	}

	public static class Registry {
		private static final Map<Identifier, OperatorBlockPseudoTag> REGISTRY = new HashMap<>();

		public static void register(Identifier id, OperatorBlockPseudoTag tag) {
			REGISTRY.compute(id, (key, existing) -> {
				if (existing != null)
					return existing.combine(tag);
				return tag;
			});
		}

		public static Set<Identifier> getKeys() {
			return ImmutableSet.copyOf(REGISTRY.keySet());
		}

		public static boolean containsKey(Identifier id) {
			return REGISTRY.containsKey(id);
		}

		public static OperatorBlockPseudoTag get(Identifier id) {
			return REGISTRY.getOrDefault(id, EMPTY);
		}
	}

	public static class Loader extends SimplePreparableReloadListener<List<Pair<Identifier, OperatorBlockPseudoTag>>> {
		public static final Loader INSTANCE = new OperatorBlockPseudoTag.Loader();
		private final HolderLookup.Provider provider = new HolderLookup.Provider() {
			@Override
			public @NotNull Stream<ResourceKey<? extends net.minecraft.core.Registry<?>>> listRegistryKeys() {
				return Stream.of(Registries.BLOCK);
			}

			@SuppressWarnings("unchecked") // The type will be Block
			@Override
			public <T> @NotNull Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends net.minecraft.core.Registry<? extends T>> registryKey) {
				if (registryKey.equals(Registries.BLOCK)) {
					return Optional.of((HolderLookup.RegistryLookup<T>) BuiltInRegistries.BLOCK);
				}
				return Optional.empty();
			}
		};

		protected Loader() {
		}

		@Override
		protected @NotNull List<Pair<Identifier, OperatorBlockPseudoTag>> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
			List<Pair<Identifier, OperatorBlockPseudoTag>> list = new ArrayList<>();

			FileToIdConverter fileToIdConverter = FileToIdConverter.json("barricade/operator_blocks");

			Set<Map.Entry<Identifier, List<Resource>>> entries = fileToIdConverter.listMatchingResourceStacks(resourceManager).entrySet();
			for (Map.Entry<Identifier, List<Resource>> entry : entries) {
				Identifier resolved = fileToIdConverter.fileToId(entry.getKey());
				for (Resource resource : entry.getValue())
					list.add(Pair.of(resolved, load(resolved, resource)));
			}

			return list;
		}

		private OperatorBlockPseudoTag load(Identifier key, Resource value) {
			try (Reader reader = value.openAsReader()) {
				JsonElement element = JsonParser.parseReader(reader);
				JsonObject object = element.getAsJsonObject();
				return DIRECT_CODEC.decode(RegistryOps.create(JsonOps.INSTANCE, provider), object).getOrThrow().getFirst();
			} catch (Exception exception) {
				BarricadeMod.LOG.error("Couldn't read operator items from {} in resource pack {}", key, value.sourcePackId(), exception);
			}
			return null;
		}

		@Override
		protected void apply(List<Pair<Identifier, OperatorBlockPseudoTag>> pairs, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
			pairs.stream().filter(Objects::nonNull).sorted(Comparator.comparingInt(value -> value.getSecond().replace ? 1 : 0)).forEachOrdered(pair -> Registry.register(pair.getFirst(), pair.getSecond()));
		}
	}
}

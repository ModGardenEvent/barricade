package net.modgarden.barricade.client.util;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.Barricade;

import java.io.Reader;
import java.util.*;

public record OperatorBlockPseudoTag(Set<Either<ResourceLocation, ResourceKey<Block>>> blocks, boolean replace) {
    private static final Codec<Either<ResourceLocation, ResourceKey<Block>>> ITEMS_CODEC = Codec.STRING.xmap(string -> {
        if (string.startsWith("#")) {
            ResourceLocation tagLocation = ResourceLocation.parse(string.substring(1));
            return Either.left(tagLocation);
        }
        ResourceLocation itemKey = ResourceLocation.parse(string);
        return Either.right(ResourceKey.create(Registries.BLOCK, itemKey));
    }, either -> either.map(resourceLocation -> "#" + resourceLocation.toString(), key -> key.location().toString()));

    public static final Codec<OperatorBlockPseudoTag> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.withAlternative(ITEMS_CODEC.listOf().xmap(Set::copyOf, List::copyOf), ITEMS_CODEC, Set::of).fieldOf("values").forGetter(OperatorBlockPseudoTag::blocks),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(OperatorBlockPseudoTag::replace)
    ).apply(inst, OperatorBlockPseudoTag::new));
    public static final Codec<OperatorBlockPseudoTag> CODEC = ResourceLocation.CODEC.xmap(
            Registry::get,
            operatorItemPseudoTag -> Registry.REGISTRY.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() == operatorItemPseudoTag)
                    .map(Map.Entry::getKey)
                    .findFirst().orElseThrow()
    );


    public static final OperatorBlockPseudoTag EMPTY = new OperatorBlockPseudoTag(Set.of(), false);

    public OperatorBlockPseudoTag(Set<Either<ResourceLocation, ResourceKey<Block>>> blocks, boolean replace) {
        this.blocks = ImmutableSet.copyOf(blocks);
        this.replace = replace;
    }

    public boolean contains(Holder<Block> block) {
        return blocks.stream().anyMatch(e -> e.map(tag -> Registry.get(tag).contains(block), block::is));
    }

    public OperatorBlockPseudoTag combine(OperatorBlockPseudoTag other) {
        var set = new HashSet<>(other.blocks);
        if (!other.replace)
            set.addAll(blocks);
        return new OperatorBlockPseudoTag(ImmutableSet.copyOf(set), other.replace);
    }

    public static class Registry {
        private static final Map<ResourceLocation, OperatorBlockPseudoTag> REGISTRY = new HashMap<>();

        public static void register(ResourceLocation id, OperatorBlockPseudoTag tag) {
            REGISTRY.compute(id, (key, existing) -> {
                if (existing != null)
                    return existing.combine(tag);
                return tag;
            });
        }

        public static Set<ResourceLocation> getKeys() {
            return ImmutableSet.copyOf(REGISTRY.keySet());
        }

        public static boolean containsKey(ResourceLocation id) {
            return REGISTRY.containsKey(id);
        }

        public static OperatorBlockPseudoTag get(ResourceLocation id) {
            return REGISTRY.getOrDefault(id, EMPTY);
        }
    }

    public static class Loader extends SimplePreparableReloadListener<List<Pair<ResourceLocation, OperatorBlockPseudoTag>>> {
        public static final Loader INSTANCE = new OperatorBlockPseudoTag.Loader();
        protected Loader() {}

        @Override
        protected List<Pair<ResourceLocation, OperatorBlockPseudoTag>> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            List<Pair<ResourceLocation, OperatorBlockPseudoTag>> list = new ArrayList<>();

            FileToIdConverter fileToIdConverter = FileToIdConverter.json("barricade/operator_blocks");

            Set<Map.Entry<ResourceLocation, List<Resource>>> entries = fileToIdConverter.listMatchingResourceStacks(resourceManager).entrySet();
            for (Map.Entry<ResourceLocation, List<Resource>> entry : entries) {
                ResourceLocation resolved = fileToIdConverter.fileToId(entry.getKey());
                for (Resource resource : entry.getValue())
                    list.add(Pair.of(resolved, load(resolved, resource)));
            }

            return list;
        }

        private OperatorBlockPseudoTag load(ResourceLocation key, Resource value) {
            try (Reader reader = value.openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                JsonObject object = element.getAsJsonObject();
                return DIRECT_CODEC.decode(JsonOps.INSTANCE, object).getOrThrow().getFirst();
            } catch (Exception exception) {
                Barricade.LOG.error("Couldn't read operator items from {} in resource pack {}", key, value.sourcePackId(), exception);
            }
            return null;
        }

        @Override
        protected void apply(List<Pair<ResourceLocation, OperatorBlockPseudoTag>> pairs, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            pairs.stream().filter(Objects::nonNull).sorted(Comparator.comparingInt(value -> value.getSecond().replace ? 1 : 0)).forEachOrdered(pair -> Registry.register(pair.getFirst(), pair.getSecond()));
        }
    }
}

package net.modgarden.barricade.client.util;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.modgarden.barricade.Barricade;

import java.io.Reader;
import java.util.*;

public record OperatorItemPseudoTag(Set<Either<ResourceLocation, ResourceKey<Item>>> items, boolean replace) {
    public static final OperatorItemPseudoTag EMPTY = new OperatorItemPseudoTag(Set.of(), false);

    public OperatorItemPseudoTag(Set<Either<ResourceLocation, ResourceKey<Item>>> items, boolean replace) {
        this.items = ImmutableSet.copyOf(items);
        this.replace = replace;
    }

    public boolean contains(Holder<Item> item) {
        return items.stream().anyMatch(e -> e.map(tag -> Registry.get(tag).contains(item), item::is));
    }

    public OperatorItemPseudoTag combine(OperatorItemPseudoTag other) {
        var set = new HashSet<>(other.items);
        if (!other.replace)
            set.addAll(items);
        return new OperatorItemPseudoTag(ImmutableSet.copyOf(set), other.replace);
    }

    public static class Registry {
        private static final Map<ResourceLocation, OperatorItemPseudoTag> REGISTRY = new HashMap<>();

        public static void register(ResourceLocation id, OperatorItemPseudoTag tag) {
            REGISTRY.compute(id, (key, existing) -> {
                if (existing != null)
                    return existing.combine(tag);
                return tag;
            });
        }

        public static OperatorItemPseudoTag get(ResourceLocation id) {
            return REGISTRY.getOrDefault(id, EMPTY);
        }
    }

    public static class Loader extends SimplePreparableReloadListener<List<Pair<ResourceLocation, OperatorItemPseudoTag>>> {
        public static final Loader INSTANCE = new OperatorItemPseudoTag.Loader();
        protected Loader() {}

        @Override
        protected List<Pair<ResourceLocation, OperatorItemPseudoTag>> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            List<Pair<ResourceLocation, OperatorItemPseudoTag>> list = new ArrayList<>();

            FileToIdConverter fileToIdConverter = FileToIdConverter.json("barricade/operator_items");

            Set<Map.Entry<ResourceLocation, List<Resource>>> entries = fileToIdConverter.listMatchingResourceStacks(resourceManager).entrySet();
            for (Map.Entry<ResourceLocation, List<Resource>> entry : entries) {
                ResourceLocation resolved = fileToIdConverter.fileToId(entry.getKey());
                for (Resource resource : entry.getValue())
                    list.add(Pair.of(resolved, load(resolved, resource)));
            }

            return list;
        }

        private OperatorItemPseudoTag load(ResourceLocation key, Resource value) {
            Set<Either<ResourceLocation, ResourceKey<Item>>> loaded = new HashSet<>();
            OperatorItemPseudoTag retValue = null;

            try (Reader reader = value.openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                JsonObject object = element.getAsJsonObject();
                var values = Codec.STRING.listOf().decode(JsonOps.INSTANCE, object.get("values")).getOrThrow().getFirst();
                for (String v : values) {
                    if (v.split(":", 2)[1].startsWith("#")) {
                        loaded.add(Either.left(ResourceLocation.fromNamespaceAndPath(v.split(":", 2)[0], v.split(":", 2)[1])));
                        DataResult<ResourceLocation> location = ResourceLocation.read(v);
                        if (location.isError()) {
                            Barricade.LOG.warn("{} (specified in operator items from {} in resource pack {}) is not a valid operator item pseudo tag id.", v, key, value.sourcePackId());
                            continue;
                        }
                        loaded.add(Either.left(location.getOrThrow()));
                        continue;
                    }
                    DataResult<ResourceLocation> location = ResourceLocation.read(v);
                    if (location.isError()) {
                        Barricade.LOG.warn("{} (specified in operator items {} in resource pack {}) is not a valid id.", v,  key, value.sourcePackId());
                        continue;
                    }
                    loaded.add(Either.right(ResourceKey.create(Registries.ITEM, location.getOrThrow())));
                }
                retValue = new OperatorItemPseudoTag(loaded, GsonHelper.getAsBoolean(object, "replace", false));
            } catch (Exception exception) {
                Barricade.LOG.error("Couldn't read operator items from {} in resource pack {}", key, value.sourcePackId(), exception);
            }
            return retValue;
        }

        @Override
        protected void apply(List<Pair<ResourceLocation, OperatorItemPseudoTag>> pairs, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            pairs.stream().filter(Objects::nonNull).sorted(Comparator.comparingInt(value -> value.getSecond().replace ? 1 : 0)).forEachOrdered(pair -> Registry.register(pair.getFirst(), pair.getSecond()));
        }
    }
}

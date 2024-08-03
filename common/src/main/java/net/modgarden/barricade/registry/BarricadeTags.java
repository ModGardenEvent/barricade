package net.modgarden.barricade.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.Barricade;

public class BarricadeTags {
    public static class BlockTags {
        public static final TagKey<Block> DIRECTION_ALLOWED_BARRIERS = TagKey.create(Registries.BLOCK, Barricade.asResource("direction_allowed_barriers"));
    }

    public static class EntityTags {
        public static final TagKey<EntityType<?>> PASSIVES = TagKey.create(Registries.ENTITY_TYPE, Barricade.asResource("passives"));
        public static final TagKey<EntityType<?>> HOSTILES = TagKey.create(Registries.ENTITY_TYPE, Barricade.asResource("hostiles"));
    }
}

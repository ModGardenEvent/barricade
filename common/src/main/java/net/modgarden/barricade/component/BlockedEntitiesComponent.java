package net.modgarden.barricade.component;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.modgarden.barricade.Barricade;

import java.util.List;
import java.util.Objects;

public record BlockedEntitiesComponent(ResourceLocation icon, List<Either<TagKey<EntityType<?>>, Holder<EntityType<?>>>> entities, boolean inverted) {
    public static final ResourceLocation DEFAULT_TEXTURE_ID = Barricade.asResource("item/barricade/icon/unknown");
    public static final BlockedEntitiesComponent EMPTY = new BlockedEntitiesComponent(DEFAULT_TEXTURE_ID, List.of(), false);

    public static final Codec<BlockedEntitiesComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.optionalFieldOf("icon", DEFAULT_TEXTURE_ID).forGetter(BlockedEntitiesComponent::icon),
            Codec.either(TagKey.hashedCodec(Registries.ENTITY_TYPE), RegistryFixedCodec.create(Registries.ENTITY_TYPE)).listOf().fieldOf("entities").forGetter(BlockedEntitiesComponent::entities),
            Codec.BOOL.optionalFieldOf("inverted", false).forGetter(BlockedEntitiesComponent::inverted)
    ).apply(inst, BlockedEntitiesComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockedEntitiesComponent> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            BlockedEntitiesComponent::icon,
            ByteBufCodecs.either(ByteBufCodecs.fromCodec(TagKey.hashedCodec(Registries.ENTITY_TYPE)), ByteBufCodecs.holderRegistry(Registries.ENTITY_TYPE)).apply(ByteBufCodecs.list()),
            BlockedEntitiesComponent::entities,
            ByteBufCodecs.BOOL,
            BlockedEntitiesComponent::inverted,
            BlockedEntitiesComponent::new
    );

    public static BlockedEntitiesComponent fromTags(ResourceLocation textureId, List<TagKey<EntityType<?>>> tags, boolean inverted) {
        return new BlockedEntitiesComponent(textureId, tags.stream().map(Either::<TagKey<EntityType<?>>, Holder<EntityType<?>>>left).toList(), inverted);
    }

    public static BlockedEntitiesComponent fromHolders(ResourceLocation textureId, List<Holder<EntityType<?>>> entities, boolean inverted) {
        return new BlockedEntitiesComponent(textureId, entities.stream().map(Either::<TagKey<EntityType<?>>, Holder<EntityType<?>>>right).toList(), inverted);
    }

    public static BlockedEntitiesComponent fromBoth(ResourceLocation textureId, List<TagKey<EntityType<?>>> tags, List<Holder<EntityType<?>>> entities, boolean inverted) {
        ImmutableList.Builder<Either<TagKey<EntityType<?>>, Holder<EntityType<?>>>> immutable = ImmutableList.builder();
        immutable.addAll(tags.stream().map(Either::<TagKey<EntityType<?>>, Holder<EntityType<?>>>left).toList());
        immutable.addAll(entities.stream().map(Either::<TagKey<EntityType<?>>, Holder<EntityType<?>>>right).toList());
        return new BlockedEntitiesComponent(textureId, immutable.build(), inverted);
    }

    public boolean canPass(CollisionContext context) {
        if (!(context instanceof EntityCollisionContext entityContext) || entityContext.getEntity() == null)
            return true;
        Entity entity = entityContext.getEntity();
        return canPass(entity.getType());
    }

    public boolean canPass(EntityType<?> entityType) {
        return entities.stream().noneMatch(tagKeyHolderEither -> tagKeyHolderEither.map(entityType::is, holder -> entityType.builtInRegistryHolder().is(holder))) ^ inverted;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BlockedEntitiesComponent component))
            return false;
        return component.icon.equals(icon) && component.entities.equals(entities) && component.inverted == inverted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(icon, entities, inverted);
    }
}

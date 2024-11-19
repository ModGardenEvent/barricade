package net.modgarden.barricade.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.modgarden.barricade.AgnosticLootContext$Builder;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.PredicateBarrierBlock;
import net.modgarden.barricade.registry.BarricadeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record AdvancedBarrier(Optional<Component> name, BlockedDirectionsComponent directions, Optional<ResourceLocation> icon, Optional<LootItemCondition> condition) {
    public static final AdvancedBarrier DEFAULT = new AdvancedBarrier(Optional.empty(), BlockedDirectionsComponent.of(Direction.values()), Optional.empty(), Optional.empty());
    public static final ResourceLocation UNKNOWN_ICON = Barricade.asResource("barricade/icon/unknown");

    public static final Codec<AdvancedBarrier> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(AdvancedBarrier::name),
            BlockedDirectionsComponent.CODEC.optionalFieldOf("directions", BlockedDirectionsComponent.of(Direction.values())).forGetter(AdvancedBarrier::directions),
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(AdvancedBarrier::icon),
            LootItemCondition.DIRECT_CODEC.optionalFieldOf("condition").forGetter(AdvancedBarrier::condition)
    ).apply(inst, AdvancedBarrier::new));
    public static final Codec<Holder<AdvancedBarrier>> CODEC = RegistryFixedCodec.create(BarricadeRegistries.ADVANCED_BARRIER);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<AdvancedBarrier>> STREAM_CODEC = ByteBufCodecs.holderRegistry(BarricadeRegistries.ADVANCED_BARRIER);

    public AdvancedBarrier(Optional<Component> name, BlockedDirectionsComponent directions, Optional<ResourceLocation> icon, Optional<LootItemCondition> condition) {
        this.name = name;
        this.directions = directions;
        Optional<ResourceLocation> finalIcon = icon.map(resourceLocation -> resourceLocation.withPath(s -> "barricade/icon/" + s));
        if (condition.isPresent() && icon.isEmpty())
            finalIcon = Optional.of(UNKNOWN_ICON);
        this.icon = finalIcon;
        this.condition = condition;
    }


    public boolean test(
            @Nullable ServerLevel level,
            @NotNull Entity entity,
            BlockState state,
            BlockPos pos
    ) {
        return condition.isPresent() && condition.get().test(newContext(level, entity, state, pos));
    }

    @SuppressWarnings("DataFlowIssue") // allow client-side checks
    public static LootContext newContext(
            @Nullable ServerLevel level,
            @NotNull Entity entity,
            BlockState state,
            BlockPos pos
    ) {
        return ((AgnosticLootContext$Builder) new LootContext.Builder(
                new LootParams.Builder(level)
                        .withParameter(LootContextParams.THIS_ENTITY, entity)
                        .withParameter(LootContextParams.BLOCK_STATE, state)
                        .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                        .create(PredicateBarrierBlock.PARAM_SET)
        ))
                .barricade$create();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AdvancedBarrier component))
            return false;
        return component.name.equals(name) && component.directions.equals(directions) && component.icon.equals(icon) && component.condition.equals(condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, directions, icon, condition);
    }
}

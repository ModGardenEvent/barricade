package net.modgarden.barricade.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lgbt.greenhouse.silicate.api.predicate.GamePredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.block.PredicateBarrierBlock;
import net.modgarden.barricade.registry.BarricadeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record AdvancedBarrier(Optional<Component> name,
                              BlockedDirections directions,
                              Optional<Identifier> icon,
                              Optional<Holder<GamePredicate<?>>> condition) {
	public static AdvancedBarrier DEFAULT = new AdvancedBarrier(Optional.empty(), BlockedDirections.of(Direction.values()), Optional.empty(), Optional.empty());
	public static final Identifier UNKNOWN_ICON = BarricadeMod.id("barricade/icon/unknown");
	public static final AdvancedBarrier UNKNOWN = new AdvancedBarrier(Optional.empty(), BlockedDirections.of(Direction.values()), Optional.of(UNKNOWN_ICON), Optional.empty());

	public static final Codec<AdvancedBarrier> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
			ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(AdvancedBarrier::name),
			BlockedDirections.CODEC.optionalFieldOf("directions", BlockedDirections.of(Direction.values())).forGetter(AdvancedBarrier::directions),
			Identifier.CODEC.optionalFieldOf("icon").forGetter(AdvancedBarrier::icon),
			GamePredicate.CODEC
					.optionalFieldOf("condition")
					.forGetter(AdvancedBarrier::condition)
	).apply(inst, AdvancedBarrier::new));
	public static final Codec<Holder<AdvancedBarrier>> CODEC = RegistryFixedCodec.create(BarricadeRegistries.ADVANCED_BARRIER);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<AdvancedBarrier>> STREAM_CODEC = ByteBufCodecs.holderRegistry(BarricadeRegistries.ADVANCED_BARRIER);

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public AdvancedBarrier(Optional<Component> name, BlockedDirections directions, Optional<Identifier> icon, Optional<Holder<GamePredicate<?>>> condition) {
		this.name = name;
		this.directions = directions;
		Optional<Identifier> finalIcon = icon.map(Identifier -> {
			if (!Identifier.getPath().startsWith("barricade/icon")) {
				return Identifier.withPath(s -> "barricade/icon/" + s);
			} else {
				return Identifier;
			}
		});
		// Sanity check for icon
		if (condition.isPresent() && icon.isEmpty()) {
			// Warn user
			Component knownName = name.orElse(Component.literal("Unknown"));
			BarricadeMod.LOG.warn("Icon is missing for Advanced Barrier \"{}\"", knownName.getString());
			// Use unknown icon
			finalIcon = Optional.of(UNKNOWN_ICON);
		}
		this.icon = finalIcon;
		this.condition = condition;
	}


	public boolean test(
			@Nullable Level level,
			@NotNull Entity entity,
			BlockState state,
			BlockPos pos
	) throws Exception {
		return condition.isPresent() && condition.get().value().test(
				PredicateBarrierBlock.newContext(level, entity, state, pos)
		);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AdvancedBarrier(
				Optional<Component> name1, BlockedDirections directions1, Optional<Identifier> icon1,
				Optional<Holder<GamePredicate<?>>> condition1
		)))
			return false;
		return name1.equals(name) && directions1.equals(directions) && icon1.equals(icon) && condition1.equals(condition);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, directions, icon, condition);
	}
}

package net.modgarden.barricade.data;

import static net.modgarden.barricade.BarricadeMod.id;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lgbt.greenhouse.silicate.api.predicate.GamePredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.Strategy;

import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.block.PredicateBarrierBlock;
import net.modgarden.barricade.registry.BarricadeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record BarricadeData(Optional<Component> name,
                            BlockedDirections directions,
                            Optional<Identifier> icon,
                            Optional<Holder<GamePredicate<?>>> condition) {
	public static BarricadeData DEFAULT = new BarricadeData(Optional.empty(), BlockedDirections.of(Direction.values()), Optional.empty(), Optional.empty());
	public static final Holder<BarricadeData> DEFAULT_HOLDER = Holder.Reference.createStandAlone(
			new HolderOwner<>() {
				@Override
				public boolean canSerializeIn(HolderOwner<BarricadeData> context) {
					return context instanceof Registry<?> registry && registry.key().equals(BarricadeRegistries.BARRICADE);
				}
			},
			ResourceKey.create(BarricadeRegistries.BARRICADE, id("default"))
	);
	public static final Identifier UNKNOWN_ICON = id("barricade/icon/unknown");
	public static final BarricadeData UNKNOWN = new BarricadeData(Optional.empty(), BlockedDirections.of(Direction.values()), Optional.of(UNKNOWN_ICON), Optional.empty());

	public static final Codec<BarricadeData> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
			ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(BarricadeData::name),
			BlockedDirections.CODEC.optionalFieldOf("directions", BlockedDirections.of(Direction.values())).forGetter(BarricadeData::directions),
			Identifier.CODEC.optionalFieldOf("icon").forGetter(BarricadeData::icon),
			GamePredicate.CODEC
					.optionalFieldOf("condition")
					.forGetter(BarricadeData::condition)
	).apply(inst, BarricadeData::new));
	public static final Codec<Holder<BarricadeData>> CODEC = RegistryFixedCodec.create(BarricadeRegistries.BARRICADE);
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<BarricadeData>> STREAM_CODEC = ByteBufCodecs.holderRegistry(BarricadeRegistries.BARRICADE);
	public static final IdMapper<Holder<BarricadeData>> ID_MAPPER = new IdMapper<>();
	public static final Strategy<Holder<BarricadeData>> STRATEGY = Strategy.createForBlockStates(ID_MAPPER);

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public BarricadeData(Optional<Component> name, BlockedDirections directions, Optional<Identifier> icon, Optional<Holder<GamePredicate<?>>> condition) {
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
		if (!(other instanceof BarricadeData(
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

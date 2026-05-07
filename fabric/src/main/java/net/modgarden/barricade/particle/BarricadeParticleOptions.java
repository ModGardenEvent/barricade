package net.modgarden.barricade.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.modgarden.barricade.data.BlockedDirections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record BarricadeParticleOptions(
		BlockedDirections blockedDirections,
		Identifier icon,
		Optional<BlockPos> origin) implements ParticleOptions {
	private static final MapCodec<BarricadeParticleOptions> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			BlockedDirections.CODEC.fieldOf("default_directions").forGetter(BarricadeParticleOptions::blockedDirections),
			Identifier.CODEC.fieldOf("icon").forGetter(BarricadeParticleOptions::icon),
			BlockPos.CODEC.optionalFieldOf("origin").forGetter(BarricadeParticleOptions::origin)
	).apply(inst, BarricadeParticleOptions::new));
	private static final StreamCodec<RegistryFriendlyByteBuf, BarricadeParticleOptions> STREAM_CODEC = StreamCodec.composite(
			BlockedDirections.STREAM_CODEC, BarricadeParticleOptions::blockedDirections,
			Identifier.STREAM_CODEC, BarricadeParticleOptions::icon,
			ByteBufCodecs.optional(BlockPos.STREAM_CODEC), BarricadeParticleOptions::origin,
			BarricadeParticleOptions::new
	);

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public BarricadeParticleOptions(BlockedDirections blockedDirections, @Nullable Identifier icon, Optional<BlockPos> origin) {
		this.blockedDirections = blockedDirections;
		this.icon = icon;
		this.origin = origin;
	}

	@Override
	public @NotNull ParticleType<?> getType() {
		return Type.INSTANCE;
	}

	public static class Type extends ParticleType<BarricadeParticleOptions> {
		public static final Type INSTANCE = new Type();

		protected Type() {
			super(true);
		}

		@Override
		public @NotNull MapCodec<BarricadeParticleOptions> codec() {
			return CODEC;
		}

		@Override
		public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, BarricadeParticleOptions> streamCodec() {
			return STREAM_CODEC;
		}
	}
}

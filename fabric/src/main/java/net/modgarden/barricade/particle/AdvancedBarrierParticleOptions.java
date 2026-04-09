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

public record AdvancedBarrierParticleOptions(
		BlockedDirections blockedDirections,
		Identifier icon,
		Optional<BlockPos> origin) implements ParticleOptions {
	private static final MapCodec<AdvancedBarrierParticleOptions> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			BlockedDirections.CODEC.fieldOf("default_directions").forGetter(AdvancedBarrierParticleOptions::blockedDirections),
			Identifier.CODEC.fieldOf("icon").forGetter(AdvancedBarrierParticleOptions::icon),
			BlockPos.CODEC.optionalFieldOf("origin").forGetter(AdvancedBarrierParticleOptions::origin)
	).apply(inst, AdvancedBarrierParticleOptions::new));
	private static final StreamCodec<RegistryFriendlyByteBuf, AdvancedBarrierParticleOptions> STREAM_CODEC = StreamCodec.composite(
			BlockedDirections.STREAM_CODEC, AdvancedBarrierParticleOptions::blockedDirections,
			Identifier.STREAM_CODEC, AdvancedBarrierParticleOptions::icon,
			ByteBufCodecs.optional(BlockPos.STREAM_CODEC), AdvancedBarrierParticleOptions::origin,
			AdvancedBarrierParticleOptions::new
	);

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public AdvancedBarrierParticleOptions(BlockedDirections blockedDirections, @Nullable Identifier icon, Optional<BlockPos> origin) {
		this.blockedDirections = blockedDirections;
		this.icon = icon;
		this.origin = origin;
	}

	@Override
	public @NotNull ParticleType<?> getType() {
		return Type.INSTANCE;
	}

	public static class Type extends ParticleType<AdvancedBarrierParticleOptions> {
		public static final Type INSTANCE = new Type();

		protected Type() {
			super(true);
		}

		@Override
		public @NotNull MapCodec<AdvancedBarrierParticleOptions> codec() {
			return CODEC;
		}

		@Override
		public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, AdvancedBarrierParticleOptions> streamCodec() {
			return STREAM_CODEC;
		}
	}
}

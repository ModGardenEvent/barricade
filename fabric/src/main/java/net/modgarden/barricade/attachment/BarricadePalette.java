package net.modgarden.barricade.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.modgarden.barricade.data.BarricadeData;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.chunk.PalettedContainer;

public record BarricadePalette(PalettedContainer<Holder<BarricadeData>> palettedContainer) {
	public static final Codec<PalettedContainer<Holder<BarricadeData>>> PALETTED_CONTAINER_CODEC = PalettedContainer.codecRW(
			BarricadeData.CODEC,
			BarricadeData.STRATEGY,
			BarricadeData.DEFAULT_HOLDER
	);
	public static final Codec<BarricadePalette> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PALETTED_CONTAINER_CODEC.fieldOf("paletted_container").forGetter(BarricadePalette::palettedContainer)
	).apply(instance, BarricadePalette::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, BarricadePalette> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.fromCodecWithRegistries(PALETTED_CONTAINER_CODEC),
			BarricadePalette::palettedContainer,
			BarricadePalette::new
	);
}

package net.modgarden.barricade.network.clientbound;


import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.data.BarricadeData;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientboundSyncDynamicRegistriesPayload(Int2ObjectMap<Holder<BarricadeData>> holderIdMap) implements CustomPacketPayload {
	public static final Identifier ID = BarricadeMod.id("sync_dynamic_registries");
	public static final Type<ClientboundSyncDynamicRegistriesPayload> TYPE = new Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncDynamicRegistriesPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(Int2ObjectOpenHashMap::new, ByteBufCodecs.VAR_INT, BarricadeData.STREAM_CODEC),
			ClientboundSyncDynamicRegistriesPayload::holderIdMap,
			ClientboundSyncDynamicRegistriesPayload::new
	);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

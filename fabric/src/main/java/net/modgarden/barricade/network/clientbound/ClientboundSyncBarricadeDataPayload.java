package net.modgarden.barricade.network.clientbound;


import net.modgarden.barricade.BarricadeMod;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientboundSyncBarricadeDataPayload(BlockPos pos, int id) implements CustomPacketPayload {
	public static final Identifier ID = BarricadeMod.id("sync_barricade_data");
	public static final Type<ClientboundSyncBarricadeDataPayload> TYPE = new Type<>(ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncBarricadeDataPayload> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ClientboundSyncBarricadeDataPayload::pos,
			ByteBufCodecs.VAR_INT, ClientboundSyncBarricadeDataPayload::id,
			ClientboundSyncBarricadeDataPayload::new
	);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

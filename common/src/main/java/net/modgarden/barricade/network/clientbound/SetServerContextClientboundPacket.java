package net.modgarden.barricade.network.clientbound;

import house.greenhouse.greenhouseconfig.impl.GreenhouseConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.modgarden.barricade.Barricade;
import org.jetbrains.annotations.NotNull;

public record SetServerContextClientboundPacket() implements CustomPacketPayload {
	public static final ResourceLocation ID = GreenhouseConfig.asResource("set_server_context");
	public static final Type<SetServerContextClientboundPacket> TYPE = new Type<>(ID);
	public static final StreamCodec<FriendlyByteBuf, SetServerContextClientboundPacket> STREAM_CODEC = StreamCodec.of(SetServerContextClientboundPacket::write, SetServerContextClientboundPacket::read);

	public static SetServerContextClientboundPacket read(FriendlyByteBuf buf) {
		return new SetServerContextClientboundPacket();
	}

	public static void write(FriendlyByteBuf buf, SetServerContextClientboundPacket packet) {
	}

	public void handle() {
		Minecraft.getInstance().execute(() -> Barricade.serverContext = true);
	}

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

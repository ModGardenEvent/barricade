package net.modgarden.barricade.neoforge.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.modgarden.barricade.network.clientbound.SetServerContextClientboundPacket;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record SyncGreenhouseConfigTask(ServerConfigurationPacketListener listener) implements ICustomConfigurationTask {
	public static final Type TYPE = new Type(SetServerContextClientboundPacket.ID);

	@Override
	public void run(Consumer<CustomPacketPayload> sender) {
		sender.accept(new SetServerContextClientboundPacket());
		listener.finishCurrentTask(TYPE);
	}

	@Override
	public @NotNull Type type() {
		return TYPE;
	}
}

package net.modgarden.barricade.client.platform;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class BarricadeClientPlatformHelperFabric implements BarricadeClientPlatformHelper {
	@Override
	public void sendSuccessClient(CommandContext<?> context, Component component) {
		((FabricClientCommandSource) context.getSource()).sendFeedback(component);
	}

	@Override
	public void sendFailureClient(CommandContext<?> context, Component component) {
		((FabricClientCommandSource) context.getSource()).sendError(component);
	}
}

package net.modgarden.barricade.client.platform;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.chat.Component;

public interface BarricadeClientPlatformHelper {
	void sendSuccessClient(CommandContext<?> context, Component component);

	void sendFailureClient(CommandContext<?> context, Component component);
}

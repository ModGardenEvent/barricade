package net.modgarden.barricade.fabric.client.platform;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.modgarden.barricade.client.platform.BarricadeClientPlatformHelper;

import java.util.Collection;

public class BarricadeClientPlatformHelperFabric implements BarricadeClientPlatformHelper {
	@Override
	public Collection<BlockElement> fixSeamsOnNeoForge(Collection<BlockElement> collection, TextureAtlasSprite textureAtlasSprite) {
		return collection;
	}

	@Override
	public void sendSuccessClient(CommandContext<?> context, Component component) {
		((FabricClientCommandSource) context.getSource()).sendFeedback(component);
	}

	@Override
	public void sendFailureClient(CommandContext<?> context, Component component) {
		((FabricClientCommandSource) context.getSource()).sendError(component);
	}

}

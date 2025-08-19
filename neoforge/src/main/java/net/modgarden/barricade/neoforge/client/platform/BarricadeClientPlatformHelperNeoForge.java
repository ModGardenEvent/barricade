package net.modgarden.barricade.neoforge.client.platform;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.modgarden.barricade.client.platform.BarricadeClientPlatformHelper;
import net.neoforged.neoforge.client.ClientCommandSourceStack;
import net.neoforged.neoforge.client.ClientHooks;

import java.util.ArrayList;
import java.util.Collection;

public class BarricadeClientPlatformHelperNeoForge implements BarricadeClientPlatformHelper {
	@Override
	public Collection<BlockElement> fixSeamsOnNeoForge(Collection<BlockElement> collection, TextureAtlasSprite textureAtlasSprite) {
		return ClientHooks.fixItemModelSeams(new ArrayList<>(collection), textureAtlasSprite);
	}

	@Override
	public void sendSuccessClient(CommandContext<?> context, Component component) {
		((ClientCommandSourceStack) context.getSource()).sendSuccess(() -> component, false);
	}

	@Override
	public void sendFailureClient(CommandContext<?> context, Component component) {
		((ClientCommandSourceStack) context.getSource()).sendFailure(component);
	}

}

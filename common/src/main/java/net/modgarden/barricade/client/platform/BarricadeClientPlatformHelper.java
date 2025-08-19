package net.modgarden.barricade.client.platform;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import java.util.Collection;

public interface BarricadeClientPlatformHelper {

	Collection<BlockElement> fixSeamsOnNeoForge(Collection<BlockElement> collection, TextureAtlasSprite textureAtlasSprite);

	void sendSuccessClient(CommandContext<?> context, Component component);

	void sendFailureClient(CommandContext<?> context, Component component);
}

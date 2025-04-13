package net.modgarden.barricade.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;

public class BarricadeClientCommands {
	public static <T> void registerClientCommands(CommandDispatcher<T> dispatcher, CommandBuildContext context) {
		LiteralCommandNode<T> rootNode = LiteralArgumentBuilder
				.<T>literal("barricade:client")
				.build();

		VisibilityCommand.register(rootNode, context);

		dispatcher.getRoot().addChild(rootNode);
	}
}

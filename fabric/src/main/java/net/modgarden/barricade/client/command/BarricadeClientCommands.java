package net.modgarden.barricade.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.BarricadeClientConfig;

public class BarricadeClientCommands {
	public static <T> void registerClientCommands(CommandDispatcher<T> dispatcher, CommandBuildContext context) {
		LiteralCommandNode<T> rootNode = LiteralArgumentBuilder
				.<T>literal("barricade:client")
				.build();

		VisibilityCommand.register(rootNode, context);

		LiteralCommandNode<T> barrierFadeTimeNode = LiteralArgumentBuilder
				.<T>literal("barrier_fade_time")
				.then(
						RequiredArgumentBuilder
								.<T, Float>argument("seconds", FloatArgumentType.floatArg(0.0f))
								.executes(ctx -> {
									float seconds = FloatArgumentType.getFloat(ctx, "seconds");

									var oldConfig = BarricadeClient.CONFIG.get();
									var newConfig = new BarricadeClientConfig(oldConfig.everythingVisible(), oldConfig.visibleBlocks(), seconds);
									BarricadeClient.CONFIG.save(newConfig, null);

									BarricadeClient.getHelper().sendSuccessClient(ctx, Component.translatable("command.barricade.barrier_fade_time.success"));
									return 1;
								})
				)

				.build();

		// FIXME: Barrier fading
//		rootNode.addChild(barrierFadeTimeNode);

		dispatcher.getRoot().addChild(rootNode);
	}
}

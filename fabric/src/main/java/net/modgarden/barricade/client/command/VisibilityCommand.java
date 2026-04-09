package net.modgarden.barricade.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Either;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.BarricadeClientConfig;
import net.modgarden.barricade.client.util.OperatorBlockPseudoTag;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class VisibilityCommand {
	public static <T> void register(LiteralCommandNode<T> root, CommandBuildContext context) {
		LiteralCommandNode<T> visibility = LiteralArgumentBuilder
				.<T>literal("visibility")
				.build();

		LiteralCommandNode<T> enable = LiteralArgumentBuilder
				.<T>literal("enable")
				.executes(VisibilityCommand::enableAll)
				.build();

		blocksArg(enable, visibility);

		LiteralCommandNode<T> disable = LiteralArgumentBuilder
				.<T>literal("disable")
				.executes(VisibilityCommand::disableAll)
				.build();

		blocksArg(disable, visibility);

		root.addChild(visibility);
	}

	private static <T> void blocksArg(LiteralCommandNode<T> parent, LiteralCommandNode<T> visibility) {
		ArgumentCommandNode<T, Identifier> blocks = RequiredArgumentBuilder
				.<T, Identifier>argument("blocks", IdentifierArgument.id())
				.suggests((ctx, builder) ->
						SharedSuggestionProvider.suggestResource(OperatorBlockPseudoTag.Registry.getKeys(), builder)
				).executes(VisibilityCommand::enableSpecific)
				.build();

		parent.addChild(blocks);
		visibility.addChild(parent);
	}

	public static int enableAll(CommandContext<?> context) {
		if (BarricadeClient.CONFIG.get().everythingVisible()) {
			BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.enable.all.error.already_enabled"));
			return 0;
		}

		var newConfig = new BarricadeClientConfig(true, Set.of(), BarricadeClient.CONFIG.get().barrierFadeTime());
		BarricadeClient.CONFIG.save(newConfig, null);

		BarricadeClient.getHelper().sendSuccessClient(context, Component.translatable("command.barricade.visibility.enable.all.success"));
		return 1;
	}

	public static int disableAll(CommandContext<?> context) {
		if (!BarricadeClient.CONFIG.get().everythingVisible()) {
			BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.disable.all.error.already_disabled"));
			return 0;
		}
		var newConfig = new BarricadeClientConfig(false, Set.of(), BarricadeClient.CONFIG.get().barrierFadeTime());
		BarricadeClient.CONFIG.save(newConfig, null);

		BarricadeClient.getHelper().sendSuccessClient(context, Component.translatable("command.barricade.visibility.disable.all.success"));
		return 1;
	}

	public static int enableSpecific(CommandContext<?> context) {
		var oldConfig = BarricadeClient.CONFIG.get();

		BlocksTagResult blocksTagResult = getBlocksTagResult(context);
		if (blocksTagResult == null) return 0;

		if (oldConfig.visibleBlocks().contains(blocksTagResult.tag()) || oldConfig.everythingVisible()) {
			BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.enable.blocks.error.already_enabled", blocksTagResult.id().toString()));
			return 0;
		}

		Set<Either<Identifier, ResourceKey<Block>>> newSpecifics = new HashSet<>(oldConfig.visibleBlocks());
		newSpecifics.add(blocksTagResult.tag());
		var newConfig = new BarricadeClientConfig(false, newSpecifics, oldConfig.barrierFadeTime());
		BarricadeClient.CONFIG.save(newConfig, null);

		BarricadeClient.getHelper().sendSuccessClient(context, Component.translatable("command.barricade.visibility.enable.blocks.success", blocksTagResult.id().toString()));
		return 1;
	}

	public static int disableSpecific(CommandContext<?> context) {
		var oldConfig = BarricadeClient.CONFIG.get();
		if (oldConfig.everythingVisible()) {
			BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.disable.blocks.error.everything_enabled"));
			return 0;
		}

		BlocksTagResult blocksTagResult = getBlocksTagResult(context);
		if (blocksTagResult == null) return 0;


		if (!oldConfig.visibleBlocks().contains(blocksTagResult.tag())) {
			BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.disable.blocks.error.already_disabled", blocksTagResult.id().toString()));
			return 0;
		}

		Set<Either<Identifier, ResourceKey<Block>>> newSpecifics = new HashSet<>(oldConfig.visibleBlocks());
		newSpecifics.remove(blocksTagResult.tag());
		var newConfig = new BarricadeClientConfig(false, newSpecifics, oldConfig.barrierFadeTime());
		BarricadeClient.CONFIG.save(newConfig, null);

		BarricadeClient.getHelper().sendSuccessClient(context, Component.translatable("command.barricade.visibility.disable.blocks.success", blocksTagResult.id().toString()));
		return 1;
	}

	private static @Nullable BlocksTagResult getBlocksTagResult(CommandContext<?> context) {
		Identifier id = context.getArgument("blocks", Identifier.class);

		if (!OperatorBlockPseudoTag.Registry.containsKey(id)) {
			BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.blocks.error.not_found", id.toString()));
			return null;
		}
		Either<Identifier, ResourceKey<Block>> tag = Either.left(id);
		return new BlocksTagResult(id, tag);
	}

	private record BlocksTagResult(Identifier id, Either<Identifier, ResourceKey<Block>> tag) {
	}
}

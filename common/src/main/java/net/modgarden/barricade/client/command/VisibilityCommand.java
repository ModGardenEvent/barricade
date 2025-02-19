package net.modgarden.barricade.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Either;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.BarricadeClientConfig;
import net.modgarden.barricade.client.util.OperatorBlockPseudoTag;

import java.util.ArrayList;
import java.util.List;

public class VisibilityCommand {
    public static void register(CommandNode<Object> root, CommandBuildContext context) {
        LiteralCommandNode<Object> visibility = LiteralArgumentBuilder
                .literal("visibility")
                .build();

        LiteralCommandNode<Object> enable = LiteralArgumentBuilder
                .literal("enable")
                .executes(VisibilityCommand::enableAll)
                .build();

        ArgumentCommandNode<Object, ResourceLocation> enableBlocks = RequiredArgumentBuilder.argument("blocks", ResourceLocationArgument.id())
                .suggests((ctx, builder) ->
                        SharedSuggestionProvider.suggestResource(OperatorBlockPseudoTag.Registry.getKeys(), builder)
                ).executes(VisibilityCommand::enableSpecific)
                .build();

        enable.addChild(enableBlocks);
        visibility.addChild(enable);

        LiteralCommandNode<Object> disable = LiteralArgumentBuilder
                .literal("disable")
                .executes(VisibilityCommand::disableAll)
                .build();

        ArgumentCommandNode<Object, ResourceLocation> disableBlocks = RequiredArgumentBuilder.argument("blocks", ResourceLocationArgument.id())
                .suggests((ctx, builder) ->
                        SharedSuggestionProvider.suggestResource(OperatorBlockPseudoTag.Registry.getKeys(), builder)
                ).executes(VisibilityCommand::disableSpecific)
                .build();


        disable.addChild(disableBlocks);
        visibility.addChild(disable);

        root.addChild(visibility);
    }

    public static int enableAll(CommandContext<?> context) {
        if (BarricadeClient.CONFIG.get().everythingVisible()) {
            BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.enable.all.error.already_enabled"));
            return 0;
        }
        var newConfig = new BarricadeClientConfig(true, List.of());
        BarricadeClient.CONFIG.saveConfig(newConfig);
        BarricadeClient.CONFIG.reloadConfig(Barricade.LOG::error);

        BarricadeClient.getHelper().sendSuccessClient(context, Component.translatable("command.barricade.visibility.enable.all.success"));
        return 1;
    }

    public static int disableAll(CommandContext<?> context) {
        if (!BarricadeClient.CONFIG.get().everythingVisible()) {
            BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.disable.all.error.already_disabled"));
            return 0;
        }
        var newConfig = new BarricadeClientConfig(false, List.of());
        BarricadeClient.CONFIG.saveConfig(newConfig);
        BarricadeClient.CONFIG.reloadConfig(Barricade.LOG::error);

        BarricadeClient.getHelper().sendSuccessClient(context, Component.translatable("command.barricade.visibility.disable.all.success"));
        return 1;
    }

    public static int enableSpecific(CommandContext<?> context) {
        var oldConfig = BarricadeClient.CONFIG.get();
        
        ResourceLocation id = context.getArgument("blocks", ResourceLocation.class);

        if (!OperatorBlockPseudoTag.Registry.containsKey(id)) {
            BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.blocks.error.not_found", id));
            return 0;
        }
        Either<OperatorBlockPseudoTag, ResourceKey<Block>> tag = Either.left(OperatorBlockPseudoTag.Registry.get(id));

        if (oldConfig.visibleBlocks().contains(Either.left(tag)) || oldConfig.everythingVisible()) {
            BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.enable.blocks.error.already_enabled", id));
            return 0;
        }

        List<Either<OperatorBlockPseudoTag, ResourceKey<Block>>> newSpecifics = new ArrayList<>(oldConfig.visibleBlocks());
        newSpecifics.add(tag);
        var newConfig = new BarricadeClientConfig(false, newSpecifics);
        BarricadeClient.CONFIG.saveConfig(newConfig);
        BarricadeClient.CONFIG.reloadConfig(Barricade.LOG::error);

        BarricadeClient.getHelper().sendSuccessClient(context, Component.translatable("command.barricade.visibility.disable.blocks.success", id));
        return 1;
    }

    public static int disableSpecific(CommandContext<?> context) {
        var oldConfig = BarricadeClient.CONFIG.get();
        if (oldConfig.everythingVisible()) {
            BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.disable.blocks.error.everything_enabled"));
            return 0;
        }

        ResourceLocation id = context.getArgument("blocks", ResourceLocation.class);

        if (!OperatorBlockPseudoTag.Registry.containsKey(id)) {
            BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.blocks.error.not_found", id));
            return 0;
        }
        Either<OperatorBlockPseudoTag, ResourceKey<Block>> tag = Either.left(OperatorBlockPseudoTag.Registry.get(id));


        if (!oldConfig.visibleBlocks().contains(Either.left(tag))) {
            BarricadeClient.getHelper().sendFailureClient(context, Component.translatable("command.barricade.visibility.disable.blocks.error.already_disabled", id));
            return 0;
        }

        List<Either<OperatorBlockPseudoTag, ResourceKey<Block>>> newSpecifics = new ArrayList<>(oldConfig.visibleBlocks());
        newSpecifics.remove(tag);
        var newConfig = new BarricadeClientConfig(false, newSpecifics);
        BarricadeClient.CONFIG.saveConfig(newConfig);
        BarricadeClient.CONFIG.reloadConfig(Barricade.LOG::error);

        BarricadeClient.getHelper().sendSuccessClient(context, Component.translatable("command.barricade.visibility.disable.blocks.success", id));
        return 1;
    }
}

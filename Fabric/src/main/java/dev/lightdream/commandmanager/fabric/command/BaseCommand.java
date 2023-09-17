package dev.lightdream.commandmanager.fabric.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import dev.lightdream.commandmanager.common.command.CommonCommandImpl;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.fabric.CommandMain;
import dev.lightdream.commandmanager.fabric.utils.PermissionUtils;
import lombok.SneakyThrows;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static net.minecraft.server.command.CommandManager.literal;

@SuppressWarnings("unused")
public abstract class BaseCommand extends CommonCommandImpl {

    public static String commandSourceFiled = "field_9819";

    private @NotNull List<ArgumentBuilder<ServerCommandSource, ?>> getArgumentsBuilders() {
        List<ArgumentBuilder<ServerCommandSource, ?>> output = new ArrayList<>();

        for (String argument : getArguments()) {
            output.add(
                    CommandManager.argument(argument, StringArgumentType.string())
                            .suggests((context, builder) -> {
                                for (String suggestion : suggest(argument)) {
                                    builder.suggest(suggestion);
                                }
                                return builder.buildFuture();
                            })
            );
        }

        return output;
    }

    protected LiteralArgumentBuilder<ServerCommandSource> getCommandBuilder(String name) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal(name);

        List<ICommonCommand> subCommands = getSubCommands();
        List<ArgumentBuilder<ServerCommandSource, ?>> arguments = getArgumentsBuilders();

        if (subCommands == null) {
            subCommands = new ArrayList<>();
        }

        for (ICommonCommand subCommandObject : subCommands) {
            BaseCommand subCommand = (BaseCommand) subCommandObject;
            for (String subName : subCommand.getNames()) {
                command.then(subCommand.getCommandBuilder(subName));
            }
        }

        List<ArgumentBuilder<ServerCommandSource, ?>> processedArguments = new ArrayList<>();

        for (ArgumentBuilder<ServerCommandSource, ?> rawArgument : arguments) {
            if (rawArgument instanceof RequiredArgumentBuilder<?, ?> requiredArgumentBuilder) {
                RequiredArgumentBuilder<ServerCommandSource, String> argument;

                try {
                    //noinspection unchecked
                    argument = (RequiredArgumentBuilder<ServerCommandSource, String>) requiredArgumentBuilder;
                } catch (Exception e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                    processedArguments.add(rawArgument);
                    continue;
                }

                if (!(argument.getType() instanceof StringArgumentType)) {
                    processedArguments.add(rawArgument);
                    continue;
                }

                SuggestionProvider<ServerCommandSource> argumentSuggestionProvider = argument.getSuggestionsProvider();

                argument.suggests(
                        (context, builder) -> {
                            SuggestionProvider<ServerCommandSource> suggestionProvider = argumentSuggestionProvider;


                            if (suggestionProvider == null) {
                                suggestionProvider = (tmpContext, tmpBuilder) -> Suggestions.empty();
                            }

                            CompletableFuture<Suggestions> suggestionsFuture =
                                    suggestionProvider.getSuggestions(context, builder);
                            Suggestions suggestions;

                            try {
                                suggestions = suggestionsFuture.get();
                            } catch (InterruptedException | ExecutionException e) {
                                return builder.buildFuture();
                            }

                            List<String> suggestionList = new ArrayList<>();

                            for (Suggestion suggestion : suggestions.getList()) {
                                suggestionList.add(suggestion.getText());
                            }

                            String value = builder.getRemaining();
                            suggestionList = ListUtils.getListThatStartsWith(suggestionList, value);

                            builder = builder.restart();

                            for (String suggestionString : suggestionList) {
                                builder.suggest(suggestionString);
                            }

                            return builder.buildFuture();
                        }
                );

                processedArguments.add(argument);
                continue;
            }

            processedArguments.add(rawArgument);
        }

        arguments = processedArguments;

        ArgumentBuilder<ServerCommandSource, ?> then = null;

        if (!arguments.isEmpty()) {
            arguments.get(arguments.size() - 1).executes(this::executeCatch);
            if (arguments.size() != 1) {
                for (int index = arguments.size() - 2; index >= 0; index--) {
                    arguments.get(index).then(arguments.get(index + 1));
                }
            }
            then = arguments.get(0);
        } else {
            command.executes(this::executeCatch);
        }

        if (then != null) {
            command.then(then);
        }

        return command;
    }

    @Override
    public final boolean registerCommand(String name) {
        ((CommandMain) getMain()).getServer().getCommandManager().getDispatcher().register(getCommandBuilder(name));
        ((CommandMain) getMain()).getServer().getPlayerManager().getPlayerList().forEach(player ->
                ((CommandMain) getMain()).getServer().getCommandManager().sendCommandTree(player)
        );

        return true;
    }

    @SneakyThrows
    private CommandOutput getSource(CommandContext<ServerCommandSource> context) {
        Field field = ServerCommandSource.class.getDeclaredField(commandSourceFiled);
        field.setAccessible(true);
        return (CommandOutput) field.get(context.getSource());
    }

    public final int executeCatch(CommandContext<ServerCommandSource> context) {
        try {
            return execute(context);
        } catch (Throwable t) {
            //noinspection CallToPrintStackTrace
            t.printStackTrace();
        }
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    public final int execute(CommandContext<ServerCommandSource> context) {
        CommandOutput source = getSource(context);
        List<String> argumentsList = convertToArgumentsList(context);

        if (!isEnabled()) {
            sendMessage(source, getMain().getLang().commandIsDisabled);
            return 0;
        }

        if (!checkPermission(source, getPermission())) {
            sendMessage(source, getMain().getLang().noPermission);
            return 0;
        }

        if (onlyForConsole()) {
            if (!(source instanceof MinecraftServer consoleSource)) {
                sendMessage(source, getMain().getLang().onlyForConsole);
                return 0;
            }
            exec(new PlatformConsole(consoleSource), argumentsList);
            return 0;
        }
        if (onlyForPlayers()) {
            if (!(source instanceof ServerPlayerEntity player)) {
                sendMessage(source, getMain().getLang().onlyFotPlayer);
                return 0;
            }
            exec(new PlatformPlayer(player), argumentsList);
            return 0;
        }

        exec(new PlatformCommandSender(source), argumentsList);
        return 0;
    }

    public List<String> convertToArgumentsList(CommandContext<ServerCommandSource> context) {
        List<String> arguments = new ArrayList<>();

        for (String argument : getArguments()) {
            arguments.add(context.getArgument(argument, String.class));
        }

        return arguments;
    }

    @Override
    public final boolean checkPermission(Object sender, String permission) {
        if (sender instanceof ServerPlayerEntity player) {
            return PermissionUtils.checkPermission((ServerPlayerEntity) sender, permission);
        }

        return sender instanceof MinecraftDedicatedServer source;
    }

    @Override
    public final void sendMessage(Object user, String message) {
        if (user instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.of(message));
            return;
        }
        if (user instanceof MinecraftDedicatedServer source) {
            source.sendMessage(net.minecraft.text.Text.of(message));
            return;
        }

        throw new RuntimeException("Can only send messages to objects of type ServerPlayerEntity and MinecraftDedicatedServer." +
                " Trying to send message to " + user.getClass());
    }
}

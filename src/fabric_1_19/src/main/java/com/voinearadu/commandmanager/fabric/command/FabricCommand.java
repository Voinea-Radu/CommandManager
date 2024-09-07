package com.voinearadu.commandmanager.fabric.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.voinearadu.commandmanager.common.command.CommonCommand;
import com.voinearadu.commandmanager.common.manager.CommonCommandManager;
import com.voinearadu.commandmanager.fabric.manager.FabricCommandManager;
import com.voinearadu.logger.Logger;
import lombok.SneakyThrows;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.literal;


public abstract class FabricCommand extends CommonCommand {

    public static final String commandSourceFiled = "field_9819";
    private final FabricCommandManager commandManager;

    public FabricCommand(CommonCommandManager commandManager) {
        super(commandManager);
        this.commandManager = (FabricCommandManager) commandManager;
    }

    public @NotNull Set<FabricCommand> getSubCommands() {
        return getPrimitiveSubCommands()
                .stream().map(command -> (FabricCommand) command)
                .collect(Collectors.toSet());
    }

    protected @NotNull List<String> getArguments() {
        return new ArrayList<>();
    }

    @SuppressWarnings("SameReturnValue")
    private int internalExecute(@NotNull CommandContext<CommandSourceStack> context) {
        try {
            CommandSource source = getSource(context);

            List<String> arguments = this.getArguments().stream()
                    .map(argument -> {
                        if (argument.startsWith("?")) {
                            argument = argument.substring(1);
                        }
                        try {
                            return context.getArgument(argument, String.class);
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .toList();

            execute(source, arguments);
        } catch (Throwable error) {
            Logger.error(error);
        }

        return 0;
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    @SneakyThrows(value = {NoSuchFieldException.class, IllegalAccessException.class})
    private CommandSource getSource(@NotNull CommandContext<CommandSourceStack> context) {
        Field field = CommandSourceStack.class.getDeclaredField(commandSourceFiled);
        field.setAccessible(true);
        return (CommandSource) field.get(context.getSource());
    }

    public LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(@NotNull String alias) {
        LiteralArgumentBuilder<CommandSourceStack> command = literal(alias);

        for (FabricCommand subCommand : getSubCommands()) {
            Logger.log("Registering subcommand(s): " + subCommand.getAliases() + " for " + alias);
            for (String subAlias : subCommand.getAliases()) {
                command = command.then(subCommand.getCommandBuilder(subAlias));
            }
        }

        List<ArgumentBuilder<CommandSourceStack, ?>> arguments = new ArrayList<>();

        for (String argument : getArguments()) {
            if (argument.startsWith("?")) {
                argument = argument.substring(1);
            }

            String finalArgument = argument;

            arguments.add(Commands
                    .argument(argument, StringArgumentType.string())
                    .suggests((context, builder) -> {
                                for (String suggestionString : onAutoComplete(finalArgument, context)) {
                                    builder.suggest(suggestionString);
                                }

                                return builder.buildFuture();
                            }
                    ));
        }

        ArgumentBuilder<CommandSourceStack, ?> then = null;

        if (!arguments.isEmpty()) {
            arguments.get(arguments.size() - 1).executes(this::internalExecute);

            if (arguments.size() != 1) {
                for (int index = arguments.size() - 2; index >= 0; index--) {
                    arguments.get(index).then(arguments.get(index + 1));

                    if (getArguments().get(index + 1).startsWith("?")) {
                        arguments.get(index).executes(this::internalExecute);
                    }
                }
            }

            then = arguments.get(0);
        } else {
            command.executes(this::internalExecute);
        }

        if (then != null) {
            command.then(then);
        }

        return command;
    }

    protected List<String> onAutoComplete(@NotNull String argument, @NotNull CommandContext<CommandSourceStack> context) {
        return new ArrayList<>();
    }

    @Override
    protected final void internalExecutePlayer(@NotNull Object player, @NotNull List<String> arguments) {
        executePlayer((ServerPlayer) player, arguments);
    }

    @Override
    protected final void internalExecuteConsole(@NotNull Object console, @NotNull List<String> arguments) {
        executeConsole((MinecraftServer) console, arguments);
    }

    @Override
    protected final void internalExecuteCommon(@NotNull Object sender, @NotNull List<String> arguments) {
        executeCommon((CommandSource) sender, arguments);
    }

    protected void executePlayer(@NotNull ServerPlayer player, @NotNull List<String> arguments) {

    }

    protected void executeConsole(@NotNull MinecraftServer console, @NotNull List<String> arguments) {

    }

    protected void executeCommon(@NotNull CommandSource sender, @NotNull List<String> arguments) {

    }

    protected List<String> recommendPlayersList() {
        return commandManager.getServer().getPlayerList().getPlayers().stream()
                .map(ServerPlayer::getDisplayName)
                .map(Component::getString)
                .toList();
    }
}

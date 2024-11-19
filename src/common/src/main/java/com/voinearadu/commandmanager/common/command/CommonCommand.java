package com.voinearadu.commandmanager.common.command;

import com.voinearadu.commandmanager.common.annotation.Command;
import com.voinearadu.commandmanager.common.exception.CommandNotAnnotated;
import com.voinearadu.commandmanager.common.manager.CommonCommandManager;
import com.voinearadu.utils.logger.Logger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class CommonCommand {

    // Provided
    private final CommonCommandManager commandManager;
    private final @Getter Command annotation;
    private final Set<CommonCommand> subCommands = new HashSet<>();
    private boolean enabled = true;

    public CommonCommand(CommonCommandManager commandManager) throws CommandNotAnnotated {
        this.commandManager = commandManager;
        if (!this.getClass().isAnnotationPresent(Command.class)) {
            throw new CommandNotAnnotated("Command " + this.getClass().getName() + " is not annotated with @Command.");
        }

        this.annotation = this.getClass().getAnnotation(Command.class);

        subCommands.addAll(commandManager.getReflectionsCrawler().getOfType(CommonCommand.class)
                .stream()
                .filter(commandClass -> commandClass.isAnnotationPresent(Command.class))
                .filter(commandClass -> commandClass.getAnnotation(Command.class).parent().equals(this.getClass()))
                .collect(Collectors.toSet())
                .stream().map(commandClass -> {
                    try {
                        return commandClass.getConstructor(CommonCommandManager.class).newInstance(commandManager);
                    } catch (Exception error) {
                        Logger.error("There was an error while registering sub command " + commandClass.getName() + " for command(s) " + this.getAliases());
                        Logger.error(error);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
    }

    public void enable() {
        if (annotation.parent() == CommonCommand.class) {
            Logger.log("Enabling command(s): " + this.getAliases());
        } else {
            CommonCommand parentCommand = commandManager.getCommand(annotation.parent());

            if (parentCommand == null) {
                throw new RuntimeException("Parent command " + annotation.parent().getName() + " of subcommand " + this.getAliases() + " is not registered.");
            }

            Logger.log("Enabling subcommand(s) of " + parentCommand.getAliases() + ": " + this.getAliases());
        }

        this.enabled = true;
        for (CommonCommand subCommand : this.getPrimitiveSubCommands()) {
            subCommand.enable();
        }
    }

    public void disable() {
        if (annotation.parent() == CommonCommand.class) {
            Logger.log("Disabling command(s): " + this.getAliases());
        } else {
            CommonCommand parentCommand = commandManager.getCommand(annotation.parent());

            if (parentCommand == null) {
                throw new RuntimeException("Parent command " + annotation.parent().getName() + " of subcommand " + this.getAliases() + " is not registered.");
            }

            Logger.log("Enabling subcommand(s) of " + parentCommand.getAliases() + ": " + this.getAliases());
        }

        this.enabled = false;
        for (CommonCommand subCommand : this.getPrimitiveSubCommands()) {
            subCommand.disable();
        }
    }

    public void execute(@NotNull Object executor, @NotNull List<String> arguments) {
        if (!enabled) {
            return;
        }

        if (!commandManager.checkPermission(executor, getPermission())) {
            commandManager.sendMessage(executor, "You don't have permission to execute this command.");
            return;
        }

        if (!arguments.isEmpty()) {
            String subCommandName = arguments.getFirst();
            CommonCommand subCommand = subCommands.stream()
                    .filter(command -> command.getAliases().contains(subCommandName))
                    .findFirst()
                    .orElse(null);

            if (subCommand != null) {
                subCommand.execute(executor, arguments.subList(1, arguments.size()));
                return;
            }
        }

        if (annotation.onlyFor().equals(Command.OnlyFor.PLAYERS)) {
            if (commandManager.getPlayerClass().isInstance(executor)) {
                internalExecutePlayer(commandManager.getPlayerClass().cast(executor), arguments);
                return;
            }
            commandManager.sendMessage(executor, "This command can only be executed by players.");
            return;
        }

        if (annotation.onlyFor().equals(Command.OnlyFor.CONSOLE)) {
            if (commandManager.getConsoleClass().isInstance(executor)) {
                internalExecuteConsole(commandManager.getConsoleClass().cast(executor), arguments);
                return;
            }
            commandManager.sendMessage(executor, "This command can only be executed by a console.");
            return;
        }

        if (commandManager.getSenderClass().isInstance(executor)) {
            internalExecuteCommon(commandManager.getSenderClass().cast(executor), arguments);
            return;
        }

        Logger.error(executor.getClass() + " is not a known executor type for the current platform.");
    }


    protected abstract void internalExecutePlayer(@NotNull Object player, @NotNull List<String> arguments);

    protected abstract void internalExecuteConsole(@NotNull Object console, @NotNull List<String> arguments);

    protected abstract void internalExecuteCommon(@NotNull Object sender, @NotNull List<String> arguments);

    public @NotNull String getPermission() {
        Class<? extends CommonCommand> parentClass = annotation.parent();

        if (parentClass.equals(CommonCommand.class)) {
            return commandManager.getBasePermission() + "." + annotation.aliases()[0];
        }

        CommonCommand command = commandManager.getCommand(parentClass);

        if (command == null) {
            throw new RuntimeException("Command " + parentClass.getName() + " is not registered.");
        }

        return command.getPermission() + "." + annotation.aliases()[0];
    }

    public final @NotNull Set<CommonCommand> getPrimitiveSubCommands() {
        Set<CommonCommand> subCommands = new HashSet<>();

        for (CommonCommand subCommand : this.subCommands) {
            subCommands.add(subCommand);
            subCommands.addAll(subCommand.getPrimitiveSubCommands());
        }

        return subCommands;
    }

    public final String getMainAlias() {
        return annotation.aliases()[0];
    }

    public final List<String> getAliases() {
        return List.of(annotation.aliases());
    }

    protected final void sendMessage(Object target, String message) {
        commandManager.sendMessage(target, message);
    }

    protected final void broadcastMessage(String message) {
        commandManager.broadcastMessage(message);
    }


    protected final boolean checkPermission(Object target, String permission) {
        return commandManager.checkPermission(target, permission);
    }


}

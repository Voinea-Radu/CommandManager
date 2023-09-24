package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.dto.ArgumentList;
import dev.lightdream.commandmanager.common.enums.OnlyFor;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.*;

public interface ICommand {

    CommonCommandMain getMain();

    void setMain(CommonCommandMain commandMain);

    default void init(@NotNull CommonCommandMain main) {
        if (!getClass().isAnnotationPresent(Command.class)) {
            Logger.error("Class " + getClass().getName() + " is not annotated as @Command");
            return;
        }

        Command command = getClass().getAnnotation(Command.class);
        init(main, command);
    }

    default void init(@NotNull CommonCommandMain main, Command commandAnnotation) {
        setMain(main);

        if (!getClass().isAnnotationPresent(Command.class)) {
            Logger.error("Class " + getClass().getName() + " is not annotated as @Command");
            return;
        }

        setCommandAnnotation(commandAnnotation);

        generateSubCommands();

        // If the command is a root command (has no parent) register it
        if (commandAnnotation.parent() == ICommand.class) {
            for (String name : getNames()) {
                if (registerCommand(name)) {
                    Logger.good("Command " + name + " initialized successfully");
                } else {
                    Logger.error("Command " + name + " could not be initialized");
                }
            }
        }
    }

    default void generateSubCommands() {
        List<ICommand> subCommands = new ArrayList<>();

        for (Class<? extends ICommand> clazz : getSubCommandClasses()) {
            ICommand subCommand = getMain().getCommandManager().createCommand(clazz);
            subCommand.setParentCommand(this);

            subCommands.add(subCommand);
        }

        setSubCommands(subCommands);
    }

    ICommand getParentCommand();

    // Utility methods
    void setParentCommand(ICommand parentCommandAnnotation);

    List<ICommand> getSubCommands();

    void setSubCommands(List<ICommand> subCommands);

    default void sendUsage(PlatformCommandSender commandSender) {
        commandSender.sendMessage(getUsage());
    }

    default void sendHelp(PlatformCommandSender commandSender) {
        commandSender.sendMessage(getHelp(commandSender));
    }

    default String getHelp(PlatformCommandSender commandSender) {
        StringBuilder output = new StringBuilder();
        output.append(getPrimaryName())
                .append(" ")
                .append(getUsage());
        output.append("\n");

        for (ICommand subCommand : getSubCommands()) {
            if (commandSender.hasPermission(subCommand.getPermission())) {
                output.append(subCommand.getPrimaryName());
                output.append(" ");
                output.append(subCommand.getUsage());
                output.append("\n");
            }
        }

        return output.toString();
    }

    default String getUsage() {
        StringBuilder usage = new StringBuilder();

        for (String argument : getArguments()) {
            usage
                    .append("[")
                    .append(argument)
                    .append("] ");
        }

        return usage.toString();
    }

    default List<Class<? extends ICommand>> getSubCommandClasses() {
        List<Class<? extends ICommand>> subCommands = new ArrayList<>();

        Reflections reflections = getMain().getReflections();
        Set<Class<? extends ICommand>> classes = new HashSet<>();

        if (reflections != null) {
            for (Class<?> clazz : reflections.getTypesAnnotatedWith(Command.class)) {
                if (ICommand.class.isAssignableFrom(clazz)) {
                    //noinspection unchecked
                    classes.add((Class<? extends ICommand>) clazz);
                }
            }
        } else {
            for (Class<? extends ICommand> clazz : getMain().getCommandClassesFinal()) {
                if (ICommand.class.isAssignableFrom(clazz)) {
                    classes.add(clazz);
                }
            }
        }

        for (Class<? extends ICommand> clazz : classes) {
            if (!clazz.isAnnotationPresent(Command.class)) {
                Logger.error("Class " + clazz.getName() + " is not annotated as @Command");
                continue;
            }

            Command commandAnnotation = clazz.getAnnotation(Command.class);

            if (commandAnnotation.parent() == getClass()) {
                subCommands.add(clazz);
            }
        }

        return subCommands;
    }

    default String getPermission() {
        String basePermission = getMain().basePermission();

        if (!basePermission.endsWith(".")) {
            basePermission = basePermission + ".";
        }

        if (getParent() != ICommand.class) {
            return getParentCommand().getPermission() + "." + getPrimaryName();
        }

        return basePermission + getPrimaryName();
    }

    void disable();

    void enable();

    boolean isEnabled();

    @SuppressWarnings("unused")
    default void execute(@NotNull PlatformCommandSender source, @NotNull ArgumentList arguments) {
        if (getSubCommands().isEmpty()) {
            Logger.warn("Executing command " + getPrimaryName() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        sendHelp(source);
    }

    @SuppressWarnings("unused")
    default void execute(@NotNull PlatformConsole console, @NotNull ArgumentList arguments) {
        if (getSubCommands().isEmpty()) {
            Logger.warn("Executing command " + getPrimaryName() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        sendHelp(console);
    }

    @SuppressWarnings("unused")
    default void execute(@NotNull PlatformPlayer player, @NotNull ArgumentList arguments) {
        if (getSubCommands().isEmpty()) {
            Logger.warn("Executing command " + getPrimaryName() + " for player, but the command is not implemented. Exec type: User, CommandContext");
        }

        sendHelp(player);
    }

    default @NotNull List<String> getArguments() {
        return new ArrayList<>();
    }

    @SuppressWarnings("unused")
    default List<String> suggest(PlatformPlayer player, String argument) {
        return new ArrayList<>();
    }

    boolean registerCommand(String alias);

    Command getCommandAnnotation();

    void setCommandAnnotation(Command commandAnnotation);

    default String getPrimaryName() {
        return getNames().get(0);
    }

    default List<String> getNames() {
        return Arrays.asList(getCommandAnnotation().aliases());
    }

    default OnlyFor getOnlyFor() {
        return getCommandAnnotation().onlyFor();
    }

    default boolean onlyForConsole() {
        return getOnlyFor() == OnlyFor.CONSOLE;
    }

    default boolean onlyForPlayers() {
        return getOnlyFor() == OnlyFor.PLAYER;
    }

    default @NotNull Class<? extends ICommand> getParent() {
        return getCommandAnnotation().parent();
    }

}

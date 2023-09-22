package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.dto.ArgumentList;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.logger.Logger;
import dev.lightdream.messagebuilder.MessageBuilder;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ICommonCommand extends ICommandAnnotationWrapper {

    CommonCommandMain getMain();

    void setMain(CommonCommandMain commandMain);

    default void init(@NotNull CommonCommandMain main) {
        setMain(main);

        if (!getClass().isAnnotationPresent(Command.class)) {
            Logger.error("Class " + getClass().getName() + " is not annotated as @Command");
            return;
        }

        Command command = getClass().getAnnotation(Command.class);
        setCommandAnnotation(command);

        generateSubCommands();

        // If the command is a root command (has no parent) register it
        if (command.parent() == ICommonCommand.class) {
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
        List<ICommonCommand> subCommands = new ArrayList<>();

        for (Class<? extends ICommonCommand> clazz : getSubCommandClasses()) {
            ICommonCommand subCommand = getMain().getCommandManager().createCommand(clazz);
            subCommand.setParentCommand(this);

            subCommands.add(subCommand);
        }

        setSubCommands(subCommands);
    }

    // Platform specific
    boolean registerCommand(String alias);

    void sendMessage(Object user, String message);

    boolean checkPermission(Object user, String permission);

    ICommonCommand getParentCommand();

    // Utility methods
    void setParentCommand(ICommonCommand parentCommandAnnotation);

    List<ICommonCommand> getSubCommands();

    void setSubCommands(List<ICommonCommand> subCommands);

    // Default utility methods
    default void sendUsage(Object user) {
        StringBuilder helpCommandOutput = new StringBuilder();
        helpCommandOutput.append(getPrimaryName())
                .append(" ")
                .append(getUsage());
        helpCommandOutput.append("\n");

        for (ICommonCommand subCommand : getSubCommands()) {
            if (checkPermission(user, subCommand.getPermission())) {
                helpCommandOutput.append(subCommand.getPrimaryName());
                helpCommandOutput.append(" ");
                helpCommandOutput.append(subCommand.getUsage());
                helpCommandOutput.append("\n");
            }
        }

        sendMessage(user, new MessageBuilder(helpCommandOutput.toString()));
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

    default List<Class<? extends ICommonCommand>> getSubCommandClasses() {
        List<Class<? extends ICommonCommand>> subCommands = new ArrayList<>();

        Reflections reflections = getMain().getReflections();
        Set<Class<? extends ICommonCommand>> classes = new HashSet<>();

        if (reflections != null) {
            for (Class<?> clazz : reflections.getTypesAnnotatedWith(Command.class)) {
                if (ICommonCommand.class.isAssignableFrom(clazz)) {
                    //noinspection unchecked
                    classes.add((Class<? extends ICommonCommand>) clazz);
                }
            }
        } else {
            for (Class<? extends ICommonCommand> clazz : getMain().getCommandClassesFinal()) {
                if (ICommonCommand.class.isAssignableFrom(clazz)) {
                    classes.add(clazz);
                }
            }
        }

        for (Class<? extends ICommonCommand> clazz : classes) {
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


    default void sendMessage(Object user, MessageBuilder message) {
        sendMessage(user, message.parse());
    }

    default String getPermission() {
        String basePermission = getMain().basePermission();

        if (!basePermission.endsWith(".")) {
            basePermission = basePermission + ".";
        }

        if (getParent() != ICommonCommand.class && getParent() != null) {
            return getParentCommand().getPermission() + "." + getPrimaryName();
        }

        return basePermission + getPrimaryName();
    }

    void disable();

    void enable();

    @SuppressWarnings("unused")
    default void execute(@NotNull PlatformCommandSender source, @NotNull ArgumentList arguments) {
        if (getSubCommands().isEmpty()) {
            Logger.warn("Executing command " + getPrimaryName() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        sendUsage(source);
    }

    @SuppressWarnings("unused")
    default void execute(@NotNull PlatformConsole console, @NotNull ArgumentList arguments) {
        if (getSubCommands().isEmpty()) {
            Logger.warn("Executing command " + getPrimaryName() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        sendUsage(console);
    }

    @SuppressWarnings("unused")
    default void execute(@NotNull PlatformPlayer player, @NotNull ArgumentList arguments) {
        if (getSubCommands().isEmpty()) {
            Logger.warn("Executing command " + getPrimaryName() + " for player, but the command is not implemented. Exec type: User, CommandContext");
        }

        sendUsage(player);
    }

    default @NotNull List<String> getArguments() {
        return new ArrayList<>();
    }

    default List<String> suggest(PlatformPlayer player, String argument) {
        return new ArrayList<>();
    }
}

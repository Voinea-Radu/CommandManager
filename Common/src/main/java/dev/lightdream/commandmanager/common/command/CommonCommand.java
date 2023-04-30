package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.logger.Logger;
import dev.lightdream.messagebuilder.MessageBuilder;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface CommonCommand {

    default void init() {
        if (!getClass().isAnnotationPresent(Command.class)) {
            Logger.error("Class " + getClass().getName() + " is not annotated as @Command");
            return;
        }

        Command command = getClass().getAnnotation(Command.class);

        generateSubCommands();

        // If the command is a root command (has no parent) register it
        if (command.parent() == CommonCommand.class && command.parentUnsafe() == CommonCommand.class) {
            if (registerCommand()) {
                Logger.good("Command " + getCommand() + " initialized successfully");
            } else {
                Logger.good("Command " + getCommand() + " could not be initialized");
            }
        }

    }

    default Command getCommandAnnotation() {
        if (!getClass().isAnnotationPresent(Command.class)) {
            throw new RuntimeException("Class " + getClass().getName() + " is not annotated with @Command but is being " +
                    "registered");
        }
        return getClass().getAnnotation(Command.class);
    }

    boolean registerCommand();

    default void sendUsage(Object user) {
        StringBuilder helpCommandOutput = new StringBuilder();
        helpCommandOutput.append(getCommand())
                .append(" ")
                .append(getUsage());
        helpCommandOutput.append("\n");

        for (CommonCommand subCommand : getSubCommands()) {
            if (checkPermission(user, subCommand.getPermission())) {
                helpCommandOutput.append(subCommand.getCommand());
                helpCommandOutput.append(" ");
                helpCommandOutput.append(subCommand.getUsage());
                helpCommandOutput.append("\n");
            }
        }

        sendMessage(user, new MessageBuilder(helpCommandOutput.toString()));
    }

    default void generateSubCommands() {
        List<CommonCommand> subCommands = new ArrayList<>();

        for (Class<?> clazz : getSubCommandClasses()) {
            if (!CommonCommand.class.isAssignableFrom(clazz)) {
                Logger.error("Class " + clazz.getName() + " is not a CommonCommand");
                continue;
            }
            //noinspection unchecked
            subCommands.add(CommonCommandMain.getCommandMain(CommonCommandMain.class).getCommandManager()
                    .initCommand((Class<? extends CommonCommand>) clazz));
        }

        saveSubCommands(subCommands);
    }

    default List<Class<?>> getSubCommandClasses() {
        List<Class<?>> subCommands = new ArrayList<>();

        Reflections reflections = CommonCommandMain.getCommandMain(CommonCommandMain.class).getReflections();
        Set<Class<?>> classes;

        if (reflections != null) {
            classes = reflections.getTypesAnnotatedWith(Command.class);
        } else {
            classes = CommonCommandMain.getCommandMain(CommonCommandMain.class).getCommandClasses();
        }

        for (Class<?> clazz : classes) {
            Command commandAnnotation = clazz.getAnnotation(Command.class);

            if (commandAnnotation.parent() == getClass() ||
                    commandAnnotation.parentUnsafe() == getClass()) {
                subCommands.add(clazz);
            }
        }

        return subCommands;
    }

    List<CommonCommand> getSubCommands();

    void saveSubCommands(List<CommonCommand> subCommands);

    default void sendMessage(Object user, MessageBuilder message) {
        sendMessage(user, message.parse());
    }

    default String getCommand() {
        return getAliasList().get(0);
    }

    default String getPermission() {
        String annotationPermission = getCommandAnnotation().permission();

        if (annotationPermission != null && !annotationPermission.equals("")) {
            return annotationPermission;
        }

        String basePermission = CommonCommandMain.getCommandMain(CommonCommandMain.class).basePermission();

        if (basePermission.equals("")) {
            return "";
        }

        if (!basePermission.endsWith(".")) {
            basePermission = basePermission + ".";
        }

        return basePermission + getCommand();
    }

    default String getUsage() {
        return getCommandAnnotation().usage();
    }

    default boolean onlyForPlayers() {
        return getCommandAnnotation().onlyForPlayers();
    }

    default boolean onlyForConsole() {
        return getCommandAnnotation().onlyForConsole();
    }

    default List<String> getAliasList() {
        return Arrays.asList(getCommandAnnotation().aliases());
    }

    default int getMinimumArgs() {
        return getCommandAnnotation().minimumArgs();
    }

    boolean checkPermission(Object user, String permission);

    void sendMessage(Object user, String message);

    default List<String> getSubCommandsHelpMessage() {
        List<String> output = new ArrayList<>();
        getSubCommands().forEach(command -> output.add(
                "/" + this.getAliasList().get(0) + " " + command.getAliasList().get(0) + " " +
                        command.getUsage()
        ));
        return output;
    }

}

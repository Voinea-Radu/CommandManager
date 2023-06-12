package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.logger.Logger;
import dev.lightdream.messagebuilder.MessageBuilder;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ICommonCommand extends ICommandAnnotationWrapper {

    default void init() {
        if (!getClass().isAnnotationPresent(Command.class)) {
            Logger.error("Class " + getClass().getName() + " is not annotated as @Command");
            return;
        }

        Command command = getClass().getAnnotation(Command.class);
        setCommandAnnotation(command);

        generateSubCommands(this);

        // If the command is a root command (has no parent) register it
        if (command.parent() == ICommonCommand.class) {
            if (registerCommand()) {
                Logger.good("Command " + getName() + " initialized successfully");
            } else {
                Logger.error("Command " + getName() + " could not be initialized");
            }
        }
    }

    default void generateSubCommands(ICommonCommand parentCommand) {
        List<ICommonCommand> subCommands = new ArrayList<>();

        for (Class<? extends ICommonCommand> clazz : getSubCommandClasses()) {
            subCommands.add(
                    CommonCommandMain.getCommandMain(CommonCommandMain.class)
                            .getCommandManager()
                            .initCommand(clazz, parentCommand)
            );
        }

        setSubCommands(subCommands);
    }

    // Platform specific
    boolean registerCommand();

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
        helpCommandOutput.append(getName())
                .append(" ")
                .append(getUsage());
        helpCommandOutput.append("\n");

        for (ICommonCommand subCommand : getSubCommands()) {
            if (checkPermission(user, subCommand.getPermission())) {
                helpCommandOutput.append(subCommand.getName());
                helpCommandOutput.append(" ");
                helpCommandOutput.append(subCommand.getUsage());
                helpCommandOutput.append("\n");
            }
        }

        sendMessage(user, new MessageBuilder(helpCommandOutput.toString()));
    }

    @SuppressWarnings("unchecked")
    default List<Class<? extends ICommonCommand>> getSubCommandClasses() {
        List<Class<? extends ICommonCommand>> subCommands = new ArrayList<>();

        Reflections reflections = CommonCommandMain.getCommandMain(CommonCommandMain.class).getReflections();
        Set<Class<? extends ICommonCommand>> classes = new HashSet<>();

        if (reflections != null) {
            for (Class<?> clazz : reflections.getTypesAnnotatedWith(Command.class)) {
                if (ICommonCommand.class.isAssignableFrom(clazz)) {
                    classes.add((Class<? extends ICommonCommand>) clazz);
                }
            }
        } else {
            for (Class<? extends ICommonCommand> clazz : CommonCommandMain.getCommandMain(CommonCommandMain.class).getCommandClasses()) {
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
        String annotationPermission = getCommandAnnotation().permission();
        String basePermission = CommonCommandMain.getCommandMain(CommonCommandMain.class).basePermission();
        String commandPermission = getName();

        if (annotationPermission != null && !annotationPermission.equals("")) {
            commandPermission = annotationPermission;
        }

        if (basePermission.equals("")) {
            return commandPermission;
        }

        if (!basePermission.endsWith(".")) {
            basePermission = basePermission + ".";
        }

        if (getCommandAnnotation().parent() != ICommonCommand.class && getCommandAnnotation().parent() != null) {
            return getParentCommand().getPermission() + "." + commandPermission;
        }

        return basePermission + commandPermission;
    }


    default List<String> getSubCommandsHelpMessage() {
        List<String> output = new ArrayList<>();
        getSubCommands().forEach(command -> output.add(
                "/" + this.getName() + " " + command.getName() + " " +
                        command.getUsage()
        ));
        return output;
    }

    void disable();
    void enable();

}

package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.CommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.logger.Logger;
import dev.lightdream.messagebuilder.MessageBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface CommonCommand {

    /**
     * Called in the constructor of the command
     */
    default void init() {
        if (!getClass().isAnnotationPresent(Command.class)) {
            if(getMain().disableDeveloperLogs()){
                return;
            }
            Logger.error("Class " + getClass().getName() + " is not annotated as @Command");
            return;
        }

        Command command = getClass().getAnnotation(Command.class);
        init(command);
    }

    default void init(Command command) {
        setCommandAnnotation(command);
        generateSubCommands();

        // If the command is a root command (has no parent) register it
        if (command.parent() == CommonCommand.class) {
            registerCommand();
        }
    }

    void setCommandAnnotation(Command command);

    Command getCommandAnnotation();

    /**
     * Registers the command with the platform specific API
     */
    void registerCommand();

    /**
     * Sends the usage of the command to the user
     *
     * @param user The target user
     */
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


    /**
     * Created the objects for all sub commands
     */
    default void generateSubCommands() {
        List<CommonCommand> subCommands = new ArrayList<>();

        for (Class<?> clazz : getMain().getMapper().createReflections().getClassesAnnotatedWith(Command.class)) {
            if (getCommandAnnotation().parent() == getClass()) {
                subCommands.add(getMain().getCommandManager()
                        .initCommand((Class<? extends CommonCommand>) clazz));
            }
        }

        saveSubCommands(subCommands);
    }

    List<CommonCommand> getSubCommands();

    void saveSubCommands(List<CommonCommand> subCommands);

    /**
     * Sends a message to a user with the platform specific API
     *
     * @param user    The target user
     * @param message The message
     */
    default void sendMessage(Object user, MessageBuilder message) {
        sendMessage(user, message.parse());
    }

    /**
     * Gets the main command alias
     *
     * @return The command string
     */
    default String getCommand() {
        return getAliases().get(0);
    }

    /**
     * Gets the command permission string
     *
     * @return The permission string
     */
    default String getPermission() {
        return getCommandAnnotation().permission();
    }

    /**
     * Gets the command usage string
     *
     * @return The usage string
     */
    default String getUsage() {
        return getCommandAnnotation().usage();
    }

    /**
     * Weaves the command is only for players
     *
     * @return True if only for players
     */
    default boolean onlyForPlayers() {
        return getCommandAnnotation().onlyForPlayers();
    }

    /**
     * Weaves the command is only for console
     *
     * @return True if only for console
     */
    default boolean onlyForConsole() {
        return getCommandAnnotation().onlyForConsole();
    }

    /**
     * Gets the command aliases
     *
     * @return The aliases
     */
    default List<String> getAliases() {
        return Arrays.asList(getCommandAnnotation().aliases());
    }

    /**
     * Gets the minimum amount of arguments
     *
     * @return The minimum amount of arguments
     */
    default int getMinimumArgs() {
        return getCommandAnnotation().minimumArgs();
    }

    /**
     * Get the main instance
     *
     * @return The main instance
     */
    CommandMain getMain();

    /**
     * Checks if the user has the permission with the platform specific API
     *
     * @param user       The user
     * @param permission The permission
     * @return True if the user has the permission
     */
    boolean checkPermission(Object user, String permission);

    /**
     * Sends a message to a user with the platform specific API
     *
     * @param user    The target user
     * @param message The message
     */
    void sendMessage(Object user, String message);

    /**
     * When the command is executed asynchronously
     *
     * @return True if the command is executed asynchronously
     */
    default boolean runAsync() {
        return getCommandAnnotation().async();
    }

    /**
     * Gets the sub commands help message
     * Do NOT Override this method
     *
     * @return The sub commands help message
     */
    default List<String> getSubCommandsHelpMessage() {
        List<String> output = new ArrayList<>();
        getSubCommands().forEach(command -> output.add(
                "/" + this.getAliases().get(0) + " " + command.getAliases().get(0) + " " +
                        command.getUsage()
        ));
        return output;
    }


}

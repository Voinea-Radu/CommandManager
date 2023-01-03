package dev.lightdream.commandmanager.command;

import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.annotation.Command;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import dev.lightdream.messagebuilder.MessageBuilder;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface CommonCommand {

    /**
     * Called in the constructor of the command
     */
    default void init(Object... args) {
        if (!getClass().isAnnotationPresent(Command.class)) {
            Logger.error("Class " + getClass().getName() + " is not annotated as @Command");
            return;
        }

        Command command = getClass().getAnnotation(Command.class);


        String permission = command.permission();
        if (!permission.equals("")) {
            setPermission(permission);
        }

        if (command.parent() == Void.class) {
            registerCommand(args);
        }
    }

    /**
     * Registers the command with the platform specific API
     */
    void registerCommand(Object... args);

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
     * Gets the subcommands of the command
     *
     * @return The subcommands
     */
    default List<CommonCommand> getSubCommands() {
        List<CommonCommand> subCommands = new ArrayList<>();

        new Reflections(getMain().getPackageName()).getTypesAnnotatedWith(Command.class).forEach(aClass -> {
            if (aClass.getAnnotation(Command.class).parent().getName().equals(getClass().getName())) {
                try {
                    Object obj;
                    Debugger.info(aClass.getName() + " constructors: ");
                    for (Constructor<?> constructor : aClass.getDeclaredConstructors()) {
                        StringBuilder parameters = new StringBuilder();
                        for (Class<?> parameter : constructor.getParameterTypes()) {
                            parameters.append(parameter.getName()).append(" ");
                        }
                        if (parameters.toString().equals("")) {
                            Debugger.info("    - zero argument");
                        } else {
                            Debugger.info("    - " + parameters);
                        }
                    }
                    if (aClass.getDeclaredConstructors()[0].getParameterCount() == 0) {
                        obj = aClass.getDeclaredConstructors()[0].newInstance();
                    } else if (aClass.getDeclaredConstructors()[0].getParameterCount() == 1) {
                        obj = aClass.getDeclaredConstructors()[0].newInstance(getMain());
                    } else {
                        Logger.error("Class " + aClass.getName() + " does not have a valid constructor");
                        return;
                    }

                    subCommands.add((CommonCommand) obj);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        return subCommands;
    }


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
        return getClass().getAnnotation(Command.class).permission();
    }

    /**
     * Sets the permission string
     *
     * @param permission The permission string
     */
    void setPermission(String permission);

    /**
     * Gets the command usage string
     *
     * @return The usage string
     */
    default String getUsage() {
        return getClass().getAnnotation(Command.class).usage();
    }

    /**
     * Weaves the command is only for players
     *
     * @return True if only for players
     */
    default boolean onlyForPlayers() {
        return getClass().getAnnotation(Command.class).onlyForPlayers();
    }

    /**
     * Weaves the command is only for console
     *
     * @return True if only for console
     */
    default boolean onlyForConsole() {
        return getClass().getAnnotation(Command.class).onlyForConsole();
    }

    /**
     * Gets the command aliases
     *
     * @return The aliases
     */
    default List<String> getAliases() {
        return Arrays.asList(getClass().getAnnotation(Command.class).aliases());
    }

    /**
     * Gets the minimum amount of arguments
     *
     * @return The minimum amount of arguments
     */
    default int getMinimumArgs() {
        return getClass().getAnnotation(Command.class).minimumArgs();
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
     * @return True if the command is executed asynchronously
     */
    default boolean runAsync() {
        return getClass().getAnnotation(Command.class).async();
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

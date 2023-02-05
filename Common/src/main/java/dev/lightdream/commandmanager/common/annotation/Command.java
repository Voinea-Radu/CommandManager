package dev.lightdream.commandmanager.common.annotation;

import dev.lightdream.commandmanager.common.command.CommonCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    /**
     * The command aliases
     *
     * @return The aliases
     */
    String[] aliases();

    /**
     * The permission string
     *
     * @return The permission string
     */
    String permission() default "";

    /**
     * The command usage string
     *
     * @return The usage string
     */
    String usage() default "";

    /**
     * If the command is only for players
     *
     * @return True if only for players
     */
    boolean onlyForPlayers() default false;

    /**
     * If the command is only for console
     *
     * @return True if only for console
     */
    boolean onlyForConsole() default false;

    /**
     * Minimum number of arguments
     *
     * @return The minimum number of arguments
     */
    int minimumArgs() default 0;

    /**
     * Whether the command should be run async (ignored on spigot platform as commands there are by default async)
     *
     * @return true if you want the command to NOT block the main thread while executing or false if you want the command to block the main thread while executing
     */
    boolean async() default false;

    /**
     * The command parent. If the current command is a sub command set it to CommonCommand.class
     *
     * @return The parent command
     */
    Class<? extends CommonCommand> parent() default CommonCommand.class;

    Class<?> parentUnsafe() default CommonCommand.class;

    boolean autoRegister() default false;
}

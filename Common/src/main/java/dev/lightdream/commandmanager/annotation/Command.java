package dev.lightdream.commandmanager.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * The command aliases
     *
     * @return The aliases
     */
    String[] aliases() default {};

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
     * The parent command class
     *
     * @return The parent class
     */
    Class<?> parent() default Void.class;

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

}

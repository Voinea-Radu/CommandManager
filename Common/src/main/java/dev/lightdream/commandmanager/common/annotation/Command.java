package dev.lightdream.commandmanager.common.annotation;

import dev.lightdream.commandmanager.common.command.ICommonCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String[] aliases();

    String permission() default "";

    String usage() default "";

    boolean onlyForPlayers() default false;

    boolean onlyForConsole() default false;

    int minimumArgs() default 0;

    /**
     * Whether the command should be run async (ignored on spigot platform as commands there are by default async)
     *
     * @return true if you want the command to NOT block the main thread while executing or false if you want the command to block the main thread while executing
     */
    boolean async() default false;

    Class<? extends ICommonCommand> parent() default ICommonCommand.class;

    //Class<?> parentUnsafe() default CommonCommand.class;

    boolean autoRegister() default false;
}

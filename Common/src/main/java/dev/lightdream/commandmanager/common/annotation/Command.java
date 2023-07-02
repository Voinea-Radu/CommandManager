package dev.lightdream.commandmanager.common.annotation;

import dev.lightdream.commandmanager.common.command.ICommonCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String name();

    String[] aliases() default {};

    String permission() default "";

    String usage() default "";

    boolean onlyForPlayers() default false;

    boolean onlyForConsole() default false;

    int minimumArgs() default 0;

    Class<? extends ICommonCommand> parent() default ICommonCommand.class;

    boolean autoRegister() default false;
}

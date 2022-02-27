package dev.lightdream.commandmanager.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {

    Class<?> parent();

    String command();

    String[] aliases() default {};

    String permission() default "";

    String usage() default "";

    boolean onlyForPlayers() default false;

    boolean onlyForConsole() default false;

    int minimumArgs() default 0;


}

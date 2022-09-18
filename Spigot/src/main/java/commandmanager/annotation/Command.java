package commandmanager.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String[] aliases() default {};

    String permission() default "";

    String usage() default "";

    boolean onlyForPlayers() default false;

    boolean onlyForConsole() default false;

    Class<?> parent() default Void.class;

    int minimumArgs() default 0;

}

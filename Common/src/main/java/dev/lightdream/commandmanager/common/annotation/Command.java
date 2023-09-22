package dev.lightdream.commandmanager.common.annotation;

import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.enums.OnlyFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String[] aliases() default {};

    OnlyFor onlyFor() default OnlyFor.BOTH;

    Class<? extends ICommonCommand> parent() default ICommonCommand.class;

    boolean autoRegister() default false;
}

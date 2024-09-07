package com.voinearadu.commandmanager.common.annotation;

import com.voinearadu.commandmanager.common.command.CommonCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String[] aliases();

    OnlyFor onlyFor() default OnlyFor.BOTH;

    Class<? extends CommonCommand> parent() default CommonCommand.class;

    enum OnlyFor {
        PLAYERS,
        CONSOLE,
        BOTH
    }
}

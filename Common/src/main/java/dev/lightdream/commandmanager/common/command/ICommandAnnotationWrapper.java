package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.annotation.Command;

import java.util.Arrays;
import java.util.List;

public interface ICommandAnnotationWrapper {

    void setCommandAnnotation(Command commandAnnotation);
    Command getCommandAnnotation();


    default String getUsage() {
        return getCommandAnnotation().usage();
    }

    default boolean onlyForPlayers() {
        return getCommandAnnotation().onlyForPlayers();
    }

    default boolean onlyForConsole() {
        return getCommandAnnotation().onlyForConsole();
    }

    default List<String> getAliasList() {
        return Arrays.asList(getCommandAnnotation().aliases());
    }

    default int getMinimumArgs() {
        return getCommandAnnotation().minimumArgs();
    }

    default boolean async() {
        return getCommandAnnotation().async();
    }

    default String getCommandString() {
        return getAliasList().get(0);
    }


}

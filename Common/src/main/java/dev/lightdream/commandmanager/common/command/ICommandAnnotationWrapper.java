package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.annotation.Command;

public interface ICommandAnnotationWrapper {

    Command getCommandAnnotation();

    void setCommandAnnotation(Command commandAnnotation);

    default String[] getCommandAliases() {
        return getCommandAnnotation().aliases();
    }

    default String[] getNames() {
        String[] names = new String[getCommandAliases().length + 1];
        names[0] = getName();
        for (int i = 0; i < getCommandAliases().length; i++) {
            names[i + 1] = getCommandAliases()[i];
        }
        return names;
    }

    default String getUsage() {
        return getCommandAnnotation().usage();
    }

    default boolean onlyForPlayers() {
        return getCommandAnnotation().onlyForPlayers();
    }

    default boolean onlyForConsole() {
        return getCommandAnnotation().onlyForConsole();
    }

    default int getMinimumArgs() {
        return getCommandAnnotation().minimumArgs();
    }

    default String getName() {
        return getCommandAnnotation().name();
    }


}

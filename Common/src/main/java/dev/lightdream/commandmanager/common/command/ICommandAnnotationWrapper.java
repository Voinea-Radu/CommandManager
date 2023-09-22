package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.enums.OnlyFor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public interface ICommandAnnotationWrapper {

    Command getCommandAnnotation();

    void setCommandAnnotation(Command commandAnnotation);

    default String getPrimaryName() {
        return getNames().get(0);
    }

    default List<String> getNames() {
        return Arrays.asList(getCommandAnnotation().aliases());
    }

    default OnlyFor getOnlyFor() {
        return getCommandAnnotation().onlyFor();
    }

    default boolean onlyForConsole() {
        return getOnlyFor() == OnlyFor.CONSOLE;
    }

    default boolean onlyForPlayers() {
        return getOnlyFor() == OnlyFor.PLAYER;
    }

    default @NotNull Class<? extends ICommonCommand> getParent() {
        return getCommandAnnotation().parent();
    }

    @SuppressWarnings("unused")
    default boolean isAutoRegistrable() {
        return getCommandAnnotation().autoRegister();
    }
}

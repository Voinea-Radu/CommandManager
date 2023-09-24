package dev.lightdream.commandmanager.common.command;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;

import java.util.List;

public interface IPlatformCommand extends ICommand{

    CommonCommand getCommonCommand();

    @Override
    default CommonCommandMain getMain() {
        return getCommonCommand().getMain();
    }

    @Override
    default void setMain(CommonCommandMain commandMain) {
        getCommonCommand().setMain(commandMain);
    }

    @Override
    default ICommand getParentCommand() {
        return getCommonCommand().getParentCommand();
    }

    @Override
    default void setParentCommand(ICommand parentCommandAnnotation) {
        getCommonCommand().setParentCommand(parentCommandAnnotation);
    }

    @Override
    default List<ICommand> getSubCommands() {
        return getCommonCommand().getSubCommands();
    }

    @Override
    default void setSubCommands(List<ICommand> subCommands) {
        getCommonCommand().setSubCommands(subCommands);
    }

    @Override
    default void disable() {
        getCommonCommand().disable();
    }

    @Override
    default void enable() {
        getCommonCommand().enable();
    }

    @Override
    default Command getCommandAnnotation() {
        return getCommonCommand().getCommandAnnotation();
    }

    @Override
    default void setCommandAnnotation(Command commandAnnotation) {
        getCommonCommand().setCommandAnnotation(commandAnnotation);
    }

    @Override
    default boolean isEnabled() {
        return getCommonCommand().isEnabled();
    }

}

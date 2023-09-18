package dev.lightdream.commandmanager.common;

import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.dto.CommandLang;
import dev.lightdream.commandmanager.common.manager.CommandManager;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;


public interface CommonCommandMain<
        Player,
        CommandSender,
        ConsoleCommandSender,
        BaseCommand extends ICommonCommand
        > {

    CommandLang getLang();

    @Nullable Reflections getReflections();

    @NotNull Set<Class<? extends ICommonCommand>> getCommandClasses();

    CommandManager getCommandManager();

    /**
     * Usually project_name.command(.)
     * To disable use ""
     *
     * @return The basePermission
     */
    @NotNull String basePermission();

    @SuppressWarnings("unused")
    default void initializeCommandMain() {
        Statics.setMain(this);
    }

    default Set<Class<? extends ICommonCommand>> getCommandClassesFinal(){
        Set<Class<? extends ICommonCommand>> classes = new HashSet<>();

        if (getReflections() != null) {
            for (Class<?> clazz : getReflections().getTypesAnnotatedWith(Command.class)) {
                if (!ICommonCommand.class.isAssignableFrom(clazz)) {
                    Logger.error("Class " + clazz.getName() + " does not extend CommonCommand");
                    continue;
                }
                //noinspection unchecked
                classes.add((Class<? extends ICommonCommand>) clazz);
            }
        } else {
            classes = getCommandClasses();
        }

        return classes;
    }

    Adapter<Player, CommandSender, ConsoleCommandSender, BaseCommand> getAdapter();
}

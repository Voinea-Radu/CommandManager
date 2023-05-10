package dev.lightdream.commandmanager.common.manager;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.logger.Logger;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private final @Getter List<ICommonCommand> commands = new ArrayList<>();

    @SneakyThrows
    public CommandManager(boolean autoRegister) {
        if (autoRegister) {
            generateCommands();
        }
    }

    @SuppressWarnings("unused")
    @SneakyThrows
    public CommandManager() {
        this(true);
    }

    public void generateCommands() {
        for (Class<? extends ICommonCommand> clazz : CommonCommandMain.getCommandMain(CommonCommandMain.class).getCommandClassesFinal()) {
            Command command = clazz.getAnnotation(Command.class);
            if (command.parent() != ICommonCommand.class) {
                continue;
            }

            if (!command.autoRegister()) {
                continue;
            }

            registerCommand(clazz);
        }
    }

    public void registerCommand(Class<? extends ICommonCommand> commandClazz) {
        ICommonCommand commandObject = initCommand(commandClazz);
        if (commandObject == null) {
            return;
        }
        registerCommand(commandObject);
    }

    @SuppressWarnings("unused")
    public void registerCommand(@NotNull ICommonCommand commandObject) {
        commands.add(commandObject);
    }

    public ICommonCommand initCommand(Class<? extends ICommonCommand> commandClass) {
        ICommonCommand command;
        Constructor<ICommonCommand> constructor;

        try {
            //noinspection unchecked
            constructor = (Constructor<ICommonCommand>) commandClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            Logger.error("There is no `no args constructor` for command " + commandClass.getName());
            return null;
        }

        try {
            command = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        return command;
    }

    public ICommonCommand initCommand(Class<? extends ICommonCommand> commandClass, ICommonCommand parentCommand) {
        ICommonCommand command = initCommand(commandClass);
        command.setParentCommand(parentCommand);
        return command;
    }
}

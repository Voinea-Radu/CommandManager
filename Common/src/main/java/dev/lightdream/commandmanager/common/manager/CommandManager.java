package dev.lightdream.commandmanager.common.manager;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.logger.Logger;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager {

    private final List<ICommonCommand> commands = new ArrayList<>();
    private final CommonCommandMain<?, ?, ?, ?> main;

    @SneakyThrows
    public CommandManager(CommonCommandMain<?, ?, ?, ?> main, boolean autoRegister) {
        this.main = main;

        if (autoRegister) {
            generateCommands();
        }
    }

    @SuppressWarnings("unused")
    @SneakyThrows
    public CommandManager(CommonCommandMain<?, ?, ?, ?> commandMain) {
        this(commandMain, true);
    }

    public void generateCommands() {
        for (Class<? extends ICommonCommand> clazz : main.getCommandClassesFinal()) {
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
        for (ICommonCommand command : getCommands()) {
            if (command.getClass().equals(commandClazz)) {
                command.enable();
                return;
            }
        }

        ICommonCommand commandObject = initCommand(commandClazz);
        if (commandObject == null) {
            return;
        }

        commands.add(commandObject);
    }

    @SuppressWarnings("unused")
    public void unregisterCommand(Class<? extends ICommonCommand> commandClazz) {
        for (ICommonCommand command : getCommands()) {
            if (command.getClass().equals(commandClazz)) {
                command.disable();
                return;
            }
        }
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
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return null;
        }

        command.init(main);

        return command;
    }

    public ICommonCommand initCommand(Class<? extends ICommonCommand> commandClass, ICommonCommand parentCommand) {
        ICommonCommand command = initCommand(commandClass);
        command.setParentCommand(parentCommand);

        return command;
    }
}

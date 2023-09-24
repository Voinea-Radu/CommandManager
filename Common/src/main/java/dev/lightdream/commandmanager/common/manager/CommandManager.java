package dev.lightdream.commandmanager.common.manager;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.command.ICommand;
import dev.lightdream.logger.Logger;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager {

    private final List<ICommand> commands = new ArrayList<>();
    private final CommonCommandMain main;

    @SneakyThrows
    public CommandManager(CommonCommandMain main, boolean autoRegister) {
        this.main = main;

        if (autoRegister) {
            generateCommands();
        }
    }

    @SuppressWarnings("unused")
    @SneakyThrows
    public CommandManager(CommonCommandMain commandMain) {
        this(commandMain, true);
    }

    public void generateCommands() {
        for (Class<? extends ICommand> clazz : main.getCommandClassesFinal()) {
            Command command = clazz.getAnnotation(Command.class);
            if (command.parent() != ICommand.class) {
                continue;
            }

            registerCommand(clazz);
        }
    }

    public void registerCommand(Class<? extends ICommand> commandClazz) {
        for (ICommand command : getCommands()) {
            if (command.getClass().equals(commandClazz)) {
                command.enable();
                return;
            }
        }

        ICommand commandObject = createCommand(commandClazz);
        if (commandObject == null) {
            Logger.error("There was an error while initializing command " + commandClazz.getName());
            Logger.error("If there is no stack trace above the conversion to a platform dependent command failed");
            return;
        }

        commandObject.init(main);

        commands.add(commandObject);
    }

    @SuppressWarnings("unused")
    public void unregisterCommand(Class<? extends ICommand> commandClazz) {
        for (ICommand command : getCommands()) {
            if (command.getClass().equals(commandClazz)) {
                command.disable();
                return;
            }
        }
    }

    public ICommand createCommand(Class<? extends ICommand> commandClass) {
        ICommand command;
        Constructor<ICommand> constructor;

        try {
            //noinspection unchecked
            constructor = (Constructor<ICommand>) commandClass.getDeclaredConstructor();
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

        if (command instanceof CommonCommand) {
            CommonCommand commonCommand = (CommonCommand) command;

            return getMain().getAdapter().convertCommand(commonCommand);
        }

        return command;
    }
}

package dev.lightdream.commandmanager.common.manager;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.lambda.ScheduleUtils;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandManager {

    private final @Getter List<CommonCommand> commands;

    @SneakyThrows
    public CommandManager(boolean autoRegister) {
        commands = new ArrayList<>();

        if (autoRegister) {
            ScheduleUtils.runTaskAsync(this::generateCommands);
        }
    }


    @SneakyThrows
    public CommandManager() {
        this(true);
    }

    public void generateCommands() {
        Debugger.log("Starting the generation of commands.");
        Reflections reflections = CommonCommandMain.getCommandMain(CommonCommandMain.class).getReflections();
        Set<Class<?>> classes;

        if (reflections != null) {
            classes = reflections.getTypesAnnotatedWith(Command.class);
        } else {
            classes = CommonCommandMain.getCommandMain(CommonCommandMain.class).getCommandClasses();
        }

        Debugger.log("Processing " + classes.size() + " classes");

        for (Class<?> clazz : classes) {
            if (!CommonCommand.class.isAssignableFrom(clazz)) {
                Logger.error("Class " + clazz.getName() + " does not extend CommonCommand");
                continue;
            }

            Command command = clazz.getAnnotation(Command.class);
            if (command.parent() != CommonCommand.class || command.parentUnsafe() != CommonCommand.class) {
                continue;
            }

            //noinspection unchecked
            Class<? extends CommonCommand> commandClass = (Class<? extends CommonCommand>) clazz;

            if (!command.autoRegister()) {
                return;
            }

            registerCommand(commandClass);
        }
        Debugger.log("Generation of commands has been completed");
    }

    public void registerCommand(Class<? extends CommonCommand> commandClazz) {
        CommonCommand commandObject = initCommand(commandClazz);
        if (commandObject == null) {
            return;
        }
        commands.add(commandObject);
    }

    @SuppressWarnings("unused")
    public void registerCommand(@NotNull CommonCommand commandObject) {
        commands.add(commandObject);
    }

    public CommonCommand initCommand(Class<? extends CommonCommand> commandClass) {
        CommonCommand command;
        Constructor<CommonCommand> constructor;

        try {
            //noinspection unchecked
            constructor = (Constructor<CommonCommand>) commandClass.getDeclaredConstructor();
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
}

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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CommandManager {

    private final @Getter List<CommonCommand> commands;
    private final Object[] args;

    @SneakyThrows
    public CommandManager(Object... args) {
        this.args = args;
        commands = new ArrayList<>();

        ScheduleUtils.runTaskAsync(this::generateCommands);
    }

    private void generateCommands() {
        Reflections reflections = CommonCommandMain.getCommandMain(CommonCommandMain.class).getReflections();
        Set<Class<?>> classes;

        if (reflections != null) {
            classes = reflections.getTypesAnnotatedWith(Command.class);
        } else {
            classes = CommonCommandMain.getCommandMain(CommonCommandMain.class).getClasses();
        }

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
        //noinspection unchecked
        Constructor<CommonCommand> constructor = (Constructor<CommonCommand>) commandClass.getDeclaredConstructors()[0];

        int argumentCount = constructor.getParameterCount();

        if (argumentCount == 0) {
            try {
                command = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            if (argumentCount - 1 != args.length) {
                Logger.error("The constructor for command " + commandClass.getName() + " has " + argumentCount + " arguments, but " + args.length + " were passed to the CommandManager");
                return null;
            }

            List<Object> passedArgsList = new ArrayList<>(Arrays.asList(args));
            Object[] passedArgs = passedArgsList.toArray();

            Debugger.log("Initializing command " + commandClass.getName() + " with args " + Arrays.toString(passedArgs));

            try {
                command = constructor.newInstance(passedArgs);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }

        return command;
    }
}

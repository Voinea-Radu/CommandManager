package dev.lightdream.commandmanager.common.manager;

import dev.lightdream.commandmanager.common.CommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    public List<CommonCommand> commands;

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public CommandManager(CommandMain main, Object... args) {
        commands = new ArrayList<>();

        for (Class<?> clazz : main.getMapper().createReflections().getClassesAnnotatedWith(Command.class)) {
            if (!CommonCommand.class.isAssignableFrom(clazz)) {
                Logger.error("Class " + clazz.getName() + " does not extend CommonCommand");
                continue;
            }

            Command command = clazz.getAnnotation(Command.class);
            if (command.parent() != CommonCommand.class) {
                continue;
            }

            Class<? extends CommonCommand> commandClass = (Class<? extends CommonCommand>) clazz;

            CommonCommand commandObject = initCommand(commandClass, main, args);
            if (commandObject == null) {
                continue;
            }
            commands.add(commandObject);

        }
    }

    @SneakyThrows
    public static CommonCommand initCommand(Class<? extends CommonCommand> commandClass, CommandMain main, Object... args) {
        CommonCommand command;
        //noinspection unchecked
        Constructor<CommonCommand> constructor = (Constructor<CommonCommand>) commandClass.getDeclaredConstructors()[0];

        int argumentCount = constructor.getParameterCount();

        if (argumentCount == 0) {
            command = constructor.newInstance();
        } else {
            if (argumentCount - 1 != args.length) {
                Logger.error("The constructor for command " + commandClass.getName() + " has " + argumentCount + " arguments, but " + args.length + " were passed to the CommandManager");
                return null;
            }

            List<Object> passedArgsList = new ArrayList<>();
            passedArgsList.add(main);
            passedArgsList.addAll(Arrays.asList(args));
            Object[] passedArgs = passedArgsList.toArray();

            command = constructor.newInstance(passedArgs);
        }

        return command;
    }
}

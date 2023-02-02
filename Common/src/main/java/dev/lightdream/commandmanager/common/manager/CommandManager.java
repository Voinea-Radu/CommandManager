package dev.lightdream.commandmanager.common.manager;

import dev.lightdream.commandmanager.common.CommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private final @Getter List<CommonCommand> commands;
    private final @Getter CommandMain main;
    private final @Getter Object[] args;

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public CommandManager(CommandMain main, Object... args) {
        this.main = main;
        this.args = args;
        commands = new ArrayList<>();

        for (Class<?> clazz : main.getMapper().createReflections().getClassesAnnotatedWith(Command.class)) {
            if (!CommonCommand.class.isAssignableFrom(clazz)) {
                if(main.disableDeveloperLogs()){
                    continue;
                }
                Logger.error("Class " + clazz.getName() + " does not extend CommonCommand");
                continue;
            }

            Command command = clazz.getAnnotation(Command.class);
            if (command.parent() != CommonCommand.class) {
                continue;
            }

            Class<? extends CommonCommand> commandClass = (Class<? extends CommonCommand>) clazz;
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

    public void registerCommand(@NotNull CommonCommand commandObject) {
        commands.add(commandObject);
    }

    @SneakyThrows
    public CommonCommand initCommand(Class<? extends CommonCommand> commandClass) {
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

            Debugger.log("Initializing command " + commandClass.getName() + " with args " + Arrays.toString(passedArgs));

            command = constructor.newInstance(passedArgs);
        }

        return command;
    }
}

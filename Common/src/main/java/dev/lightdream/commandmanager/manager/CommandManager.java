package dev.lightdream.commandmanager.manager;

import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.command.CommonCommand;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    public List<CommonCommand> commands = new ArrayList<>();

    @SneakyThrows
    public CommandManager(CommandMain main, Object... args) {
        List<Class<? extends CommonCommand>> commandClasses = main.getCommandClasses();
        List<CommonCommand> commandObjects = main.getCommands();

        List<Class<? extends CommonCommand>> commandClassesToRemove = new ArrayList<>();
        List<CommonCommand> commandObjectsToRemove = new ArrayList<>();

        for (Class<? extends CommonCommand> clazz : commandClasses) {
            for (CommonCommand command : commands) {
                if (command.getClass().getName().equals(clazz.getName())) {
                    commandClassesToRemove.add(clazz);
                    break;
                }
            }
        }

        for (CommonCommand commandObject : commandObjects) {
            for (CommonCommand command : commands) {
                if (command.getClass().getName().equals(commandObject.getClass().getName())) {
                    commandObjectsToRemove.add(commandObject);
                    break;
                }
            }
        }

        commandClasses.removeAll(commandClassesToRemove);
        commandObjects.removeAll(commandObjectsToRemove);

        this.commands.addAll(registerCommands(commandClasses, commandObjects, main, args));
    }

    @SneakyThrows
    public static List<CommonCommand> registerCommands(List<Class<? extends CommonCommand>> commandClasses,
                                                       List<CommonCommand> commandObjects, CommandMain main, Object... args) {
        List<CommonCommand> commands = new ArrayList<>();

        for (Class<? extends CommonCommand> clazz : commandClasses) {
            CommonCommand commandObject;

            //noinspection unchecked
            Constructor<CommonCommand> constructor = (Constructor<CommonCommand>) clazz.getDeclaredConstructors()[0];

            int argumentCount = constructor.getParameterCount();

            if (argumentCount == 0) {
                commandObject = constructor.newInstance();
            } else {
                if (argumentCount - 1 != args.length) {
                    Logger.error("The constructor for command " + clazz.getName() + " has " + argumentCount + " arguments, but " + args.length + " were passed to the CommandManager");
                    continue;
                }

                List<Object> passedArgsList = new ArrayList<>();
                passedArgsList.add(main);
                passedArgsList.addAll(Arrays.asList(args));
                Object[] passedArgs = passedArgsList.toArray();

                commandObject = constructor.newInstance(passedArgs);
            }

            commands.add(commandObject);
        }
        commands.addAll(commandObjects);

        return commands;
    }

}

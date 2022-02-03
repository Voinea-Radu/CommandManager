package dev.lightdream.fly.manager;

import dev.lightdream.fly.CommandMain;
import dev.lightdream.fly.annotations.Command;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    public List<dev.lightdream.fly.commands.Command> commands = new ArrayList<>();

    public CommandManager(CommandMain main, String packageName) {
        new Reflections(packageName).getTypesAnnotatedWith(Command.class).forEach(aClass -> {
            try {
                for (dev.lightdream.fly.commands.Command command : commands) {
                    if (command.getClass().getSimpleName().equals(aClass.getSimpleName())) {
                        return;
                    }
                }
                Object obj;
                Debugger.info(aClass.getSimpleName() + " constructors: ");
                for (Constructor<?> constructor : aClass.getDeclaredConstructors()) {
                    StringBuilder parameters = new StringBuilder();
                    for (Class<?> parameter : constructor.getParameterTypes()) {
                        parameters.append(parameter.getSimpleName()).append(" ");
                    }
                    if (parameters.toString().equals("")) {
                        Debugger.info("    - zero argument");
                    } else {
                        Debugger.info("    - " + parameters);
                    }
                }
                if (aClass.getDeclaredConstructors()[0].getParameterCount() == 0) {
                    obj = aClass.getConstructor().newInstance();
                    //obj = aClass.getDeclaredConstructors()[0].newInstance();
                } else if (aClass.getDeclaredConstructors()[0].getParameterCount() == 1) {
                    obj = aClass.getDeclaredConstructors()[0].newInstance(main);
                } else {
                    Logger.error("Class " + aClass.getSimpleName() + " does not have a valid constructor");
                    return;
                }
                if (obj instanceof dev.lightdream.fly.commands.Command) {
                    commands.add((dev.lightdream.fly.commands.Command) obj);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

}

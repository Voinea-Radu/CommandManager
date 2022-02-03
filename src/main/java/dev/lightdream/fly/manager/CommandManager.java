package dev.lightdream.fly.manager;

import dev.lightdream.fly.CommandMain;
import dev.lightdream.fly.annotations.Command;
import dev.lightdream.logger.Logger;
import org.reflections.Reflections;

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
                if (aClass.getDeclaredConstructors()[0].getParameterCount() == 0) {
                    obj = aClass.getDeclaredConstructors()[0].newInstance();
                } else if (aClass.getDeclaredConstructors()[0].getParameterCount() == 1) {
                    obj = aClass.getDeclaredConstructors()[0].newInstance(main);
                } else {
                    Logger.error("Class " + aClass.getSimpleName() + " does not have a valid constructor");
                    return;
                }
                if (obj instanceof dev.lightdream.fly.commands.Command) {
                    commands.add((dev.lightdream.fly.commands.Command) obj);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

}

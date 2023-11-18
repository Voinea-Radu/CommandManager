package dev.lightdream.commandmanager.common.manager;

import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.dto.CommandLang;
import dev.lightdream.logger.Logger;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Accessors(chain = true, fluent = true)
public class CommonCommandManager {

    private static @Getter CommonCommandManager instance = null;

    private final List<ICommonCommand> commands = new ArrayList<>();

    private final Reflections reflections;
    private final boolean autoRegister;
    private final CommandLang lang;
    private final Set<Class<? extends ICommonCommand>> commandClasses;
    private final String basePermission;

    public static <T extends CommonBuilder> T applyDefaults(T builder) {
        //noinspection unchecked
        return (T) builder
                .lang(new CommandLang())
                .basePermission("")
                .commandClasses(new HashSet<>())
                .autoRegister(true);
    }

    public static class CommonBuilder{
        public CommonBuilder(){
        }
    }


    @Builder(builderClassName = "CommonBuilder")
    protected CommonCommandManager(Reflections reflections, CommandLang lang,
                                 Set<Class<? extends ICommonCommand>> commandClasses, String basePermission,
                                 boolean autoRegister) {
        instance = this;

        this.reflections = reflections;
        this.autoRegister = autoRegister;
        this.lang = lang;
        this.basePermission = basePermission;

        Set<Class<? extends ICommonCommand>> classes = new HashSet<>();

        if (reflections != null) {
            for (Class<?> clazz : reflections.getTypesAnnotatedWith(Command.class)) {
                if (!ICommonCommand.class.isAssignableFrom(clazz)) {
                    Logger.error("Class " + clazz.getName() + " does not extend CommonCommand");
                    continue;
                }
                //noinspection unchecked
                classes.add((Class<? extends ICommonCommand>) clazz);
            }
        } else {
            classes = commandClasses;
        }

        this.commandClasses = classes;


        if (autoRegister) {
            generateCommands();
        }
    }

    public static void generateCommands() {
        for (Class<? extends ICommonCommand> clazz : instance().commandClasses()) {
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

    public static void registerCommand(Class<? extends ICommonCommand> commandClazz) {
        for (ICommonCommand command : instance().commands()) {
            if (command.getClass().equals(commandClazz)) {
                command.enable();
                return;
            }
        }

        ICommonCommand commandObject = initCommand(commandClazz);
        if (commandObject == null) {
            return;
        }

        instance().commands().add(commandObject);
    }

    @SuppressWarnings("unused")
    public static void unregisterCommand(Class<? extends ICommonCommand> commandClazz) {
        for (ICommonCommand command : instance().commands()) {
            if (command.getClass().equals(commandClazz)) {
                command.disable();
                return;
            }
        }
    }

    public static ICommonCommand initCommand(Class<? extends ICommonCommand> commandClass) {
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

        return command;
    }

    public ICommonCommand initCommand(Class<? extends ICommonCommand> commandClass, ICommonCommand parentCommand) {
        ICommonCommand command = initCommand(commandClass);

        if(command==null){
            return null;
        }

        command.setParentCommand(parentCommand);
        return command;
    }
}

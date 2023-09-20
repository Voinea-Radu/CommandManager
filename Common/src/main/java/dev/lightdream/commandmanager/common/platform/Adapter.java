package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.commandmanager.common.command.CommonBaseCommand;
import dev.lightdream.commandmanager.common.command.ICommonCommand;

public abstract class Adapter{

    public abstract Object convertPlayer(PlatformPlayer player) ;

    public abstract <T> PlatformPlayer convertPlayer(T playerObject);


    public abstract Object convertCommandSender(PlatformCommandSender commandSender) ;

    public abstract <T> PlatformCommandSender convertCommandSender(T commandSenderObject);


    public abstract Object convertConsole(PlatformConsole console);

    public abstract <T> PlatformConsole convertConsole(T consoleObject);


    public ICommonCommand convertCommand(CommonBaseCommand command) {
        return command;
    }

    protected RuntimeException createConversionError(Class<?> fromClass, Class<?> toClass){
        return new RuntimeException("Can not convert from " + fromClass.getName() + " to " + toClass.getName());
    }

    protected RuntimeException createConversionError(Object fromObject, Class<?> toClass) {
        return createConversionError(fromObject.getClass(), toClass);
    }
}

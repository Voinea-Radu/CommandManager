package dev.lightdream.commandmanager.command;

import java.util.ArrayList;
import java.util.List;

public interface CommandProvider {


    /**
     * Allows you to pass classes that the command manager will construct and register
     * @return A list of classes
     */
    List<Class<? extends CommonCommand>> getCommandClasses();

    /**
     * Allows you to pass pre-constructed commands to the CommandManager
     * @return A list of commands
     */
    default List<CommonCommand> getCommands(){
        return new ArrayList<>();
    }

}

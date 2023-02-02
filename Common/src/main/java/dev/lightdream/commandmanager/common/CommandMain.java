package dev.lightdream.commandmanager.common;

import dev.lightdream.commandmanager.common.dto.CommandLang;
import dev.lightdream.commandmanager.common.manager.CommandManager;
import dev.lightdream.reflections.Mapper;
import org.jetbrains.annotations.NotNull;


public interface CommandMain {

    CommandLang getLang();

    @NotNull Mapper getMapper();

    CommandManager getCommandManager();

    /**
     * Only use if you know what you are doing. This will disable developer logs.
     * @return status
     */
    default boolean disableDeveloperLogs() {
        return false;
    }

}

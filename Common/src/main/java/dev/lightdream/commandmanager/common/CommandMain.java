package dev.lightdream.commandmanager.common;

import dev.lightdream.commandmanager.common.dto.CommandLang;
import dev.lightdream.commandmanager.common.manager.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;


public interface CommandMain {

    CommandLang getLang();

    @NotNull Reflections getReflections();

    CommandManager getCommandManager();

    /**
     * Only use if you know what you are doing. This will disable developer logs.
     * @return status
     */
    default boolean disableDeveloperLogs() {
        return false;
    }

    /**
     * Usually project_name.command(.)
     * To disable use ""
     * @return The basePermission
     */
    @NotNull String basePermission();

}

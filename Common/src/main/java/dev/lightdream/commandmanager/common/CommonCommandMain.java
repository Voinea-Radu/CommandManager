package dev.lightdream.commandmanager.common;

import dev.lightdream.commandmanager.common.dto.CommandLang;
import dev.lightdream.commandmanager.common.manager.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;


public interface CommonCommandMain {

    CommandLang getLang();

    @NotNull Reflections getReflections();

    CommandManager getCommandManager();

    /**
     * Usually project_name.command(.)
     * To disable use ""
     * @return The basePermission
     */
    @NotNull String basePermission();

    @SuppressWarnings("unused")
    default void initializeCommandMain() {
        Statics.setMain(this);
    }

    static <T extends CommonCommandMain> T getCommandMain(Class<T> clazz){
        return Statics.getMainAs(clazz);
    }

}

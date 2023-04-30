package dev.lightdream.commandmanager.common;

import dev.lightdream.commandmanager.common.dto.CommandLang;
import dev.lightdream.commandmanager.common.manager.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.Set;


public interface CommonCommandMain {

    static <T extends CommonCommandMain> T getCommandMain(Class<T> clazz) {
        return Statics.getMainAs(clazz);
    }

    CommandLang getLang();

    @Nullable Reflections getReflections();

    @NotNull Set<Class<?>> getCommandClasses();

    CommandManager getCommandManager();

    /**
     * Usually project_name.command(.)
     * To disable use ""
     *
     * @return The basePermission
     */
    @NotNull String basePermission();

    @SuppressWarnings("unused")
    default void initializeCommandMain() {
        Statics.setMain(this);
    }

}

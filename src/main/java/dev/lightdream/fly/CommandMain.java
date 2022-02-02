package dev.lightdream.fly;

import dev.lightdream.fly.dto.CommandLang;
import dev.lightdream.fly.manager.CommandManager;

public interface CommandMain {

    CommandLang getLang();

    void loadConfigs();

    String getProjectName();

    String getProjectVersion();

    @SuppressWarnings("unused")
    CommandManager registerCommandManager();

}

package dev.lightdream.fly;

import dev.lightdream.fly.dto.CommandLang;

public interface CommandMain {

    CommandLang getLang();

    void loadConfigs();

    String getProjectName();

    String getProjectVersion();

}

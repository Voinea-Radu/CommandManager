package commandmanager;

import commandmanager.dto.CommandLang;
import commandmanager.manager.CommandManager;

public interface CommandMain {

    CommandLang getLang();

    @SuppressWarnings("unused")
    CommandManager getCommandManager();

    String getPackageName();

}

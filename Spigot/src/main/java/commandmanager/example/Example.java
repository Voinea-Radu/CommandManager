package commandmanager.example;

import commandmanager.CommandMain;
import commandmanager.dto.CommandLang;
import commandmanager.manager.CommandManager;
import dev.lightdream.filemanager.FileManagerMain;

import java.io.File;

public class Example implements CommandMain, FileManagerMain {

    private CommandLang lang;
    private CommandManager commandManager;

    public void onEnable() {
        lang = new CommandLang();
        commandManager = new CommandManager(this);
    }

    @Override
    public CommandLang getLang() {
        return lang;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public String getPackageName() {
        return "package.name";
    }

    @Override
    public File getDataFolder() {
        return new File(System.getProperty("user.dir"));
    }
}
package dev.lightdream.commandmanager.example;


import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.dto.CommandLang;
import dev.lightdream.commandmanager.manager.CommandManager;
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
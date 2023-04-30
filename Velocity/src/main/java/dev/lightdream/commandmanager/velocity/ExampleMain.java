package dev.lightdream.commandmanager.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.lightdream.commandmanager.common.dto.CommandLang;
import dev.lightdream.commandmanager.common.manager.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

public class ExampleMain implements CommandMain {

    private final CommandLang lang;
    private final CommandManager commandManager;
    private final ProxyServer proxyServer;
    private final Reflections reflections;

    public ExampleMain(ProxyServer proxyServer, Reflections reflections) {
        lang = new CommandLang(); // This would usually be loaded from disk using a library like FileManager

        this.proxyServer = proxyServer;
        this.reflections = reflections;

        commandManager = new CommandManager();
        commandManager.registerCommand(ExampleCommand.class);
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
    public @NotNull String basePermission() {
        return "project.command";
    }

    @Override
    public @NotNull ProxyServer getProxy() {
        return proxyServer;
    }

    @Override
    public @NotNull Reflections getReflections() {
        return reflections;
    }
}

package dev.lightdream.commandmanager.spigot.platform;

import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import org.bukkit.command.ConsoleCommandSender;

public class SpigotConsole extends PlatformConsole {

    public SpigotConsole(ConsoleCommandSender consoleCommandSender, SpigotAdapter adapter) {
        super(consoleCommandSender, adapter);
    }

    @Override
    public ConsoleCommandSender getNative() {
        return (ConsoleCommandSender) this.nativeObject;
    }

    @Override
    public SpigotAdapter getAdapter() {
        return (SpigotAdapter) this.adapter;
    }
}

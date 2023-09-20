package dev.lightdream.commandmanager.spigot.platform;

import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SpigotAdapter extends Adapter {
    @Override
    public Player convertPlayer(PlatformPlayer player) {
        return (Player) player.getNativePlayer();
    }

    @Override
    public <T> SpigotPlayer convertPlayer(T playerObject) {
        if (!(playerObject instanceof Player)) {
            throw createConversionError(playerObject, SpigotPlayer.class);
        }

        Player player = (Player) playerObject;
        return new SpigotPlayer(player);
    }

    @Override
    public CommandSender convertCommandSender(PlatformCommandSender commandSender) {
        return (CommandSender) commandSender.getNativeCommandSender();
    }

    @Override
    public <T> SpigotCommandSender convertCommandSender(T commandSenderObject) {
        if (!(commandSenderObject instanceof CommandSender)) {
            throw createConversionError(commandSenderObject, SpigotCommandSender.class);
        }

        CommandSender player = (CommandSender) commandSenderObject;
        return new SpigotCommandSender(player);
    }

    @Override
    public ConsoleCommandSender convertConsole(PlatformConsole console) {
        return (ConsoleCommandSender) console.getNativeConsole();
    }

    @Override
    public <T> SpigotConsole convertConsole(T consoleObject) {
        if (!(consoleObject instanceof ConsoleCommandSender)) {
            throw createConversionError(consoleObject, SpigotConsole.class);
        }

        ConsoleCommandSender console = (ConsoleCommandSender) consoleObject;
        return new SpigotConsole(console);
    }
}

package dev.lightdream.commandmanager.spigot.platform;

import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.spigot.command.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SpigotAdapter extends Adapter {
    @Override
    public Player convertPlayer(PlatformPlayer player) {
        return (Player) player.getNativePlayer();
    }

    @Override
    public <T> PlatformPlayer convertPlayer(T playerObject) {
        if (!(playerObject instanceof Player)) {
            throw new RuntimeException("Can not convert from " + playerObject.getClass().getName() + " to " +
                    PlatformPlayer.class.getName());
        }
        Player player = (Player) playerObject;
        return new SpigotPlayer(player);
    }

    @Override
    public CommandSender convertCommandSender(PlatformCommandSender commandSender) {
        return (CommandSender) commandSender.getNativeCommandSender();
    }

    @Override
    public <T> PlatformCommandSender convertCommandSender(T commandSenderObject) {
        if (!(commandSenderObject instanceof CommandSender)) {
            throw new RuntimeException("Can not convert from " + commandSenderObject.getClass().getName() + " to " +
                    PlatformCommandSender.class.getName());
        }

        CommandSender player = (CommandSender) commandSenderObject;
        return new SpigotCommandSender(player);
    }

    @Override
    public ConsoleCommandSender convertConsole(PlatformConsole console) {
        return (ConsoleCommandSender) console.getNativeConsole();
    }

    @Override
    public <T> PlatformConsole convertConsole(T consoleObject) {
        if (!(consoleObject instanceof ConsoleCommandSender)) {
            throw new RuntimeException("Can not convert from " + consoleObject.getClass().getName() + " to " +
                    PlatformConsole.class.getName());
        }

        ConsoleCommandSender console = (ConsoleCommandSender) consoleObject;
        return new SpigotConsole(console);
    }
}

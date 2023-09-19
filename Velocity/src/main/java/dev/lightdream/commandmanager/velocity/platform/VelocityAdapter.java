package dev.lightdream.commandmanager.velocity.platform;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.velocity.command.BaseCommand;

public class VelocityAdapter extends Adapter {
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
        return new VelocityPlayer(player);
    }

    @Override
    public CommandSource convertCommandSender(PlatformCommandSender commandSender) {
        return (CommandSource) commandSender.getNativeCommandSender();
    }

    @Override
    public <T> PlatformCommandSender convertCommandSender(T commandSenderObject) {
        if (!(commandSenderObject instanceof CommandSource)) {
            throw new RuntimeException("Can not convert from " + commandSenderObject.getClass().getName() + " to " +
                    PlatformCommandSender.class.getName());
        }

        CommandSource player = (CommandSource) commandSenderObject;
        return new VelocityCommandSender(player);
    }

    @Override
    public ConsoleCommandSource convertConsole(PlatformConsole console) {
        return (ConsoleCommandSource) console.getNativeConsole();
    }

    @Override
    public <T> PlatformConsole convertConsole(T consoleObject) {
        if (!(consoleObject instanceof ConsoleCommandSource)) {
            throw new RuntimeException("Can not convert from " + consoleObject.getClass().getName() + " to " +
                    PlatformConsole.class.getName());
        }

        ConsoleCommandSource console = (ConsoleCommandSource) consoleObject;
        return new VelocityConsole(console);
    }

}

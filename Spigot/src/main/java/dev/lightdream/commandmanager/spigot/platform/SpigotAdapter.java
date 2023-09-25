package dev.lightdream.commandmanager.spigot.platform;

import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.spigot.command.SpigotCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public interface SpigotAdapter extends Adapter<Player, ConsoleCommandSender, CommandSender> {

    @Override
    default SpigotPlayer convertPlayer(Player nativePlayer) {
        return new SpigotPlayer(nativePlayer, this);
    }

    @Override
    default SpigotConsole convertConsole(ConsoleCommandSender nativeConsole) {
        return new SpigotConsole(nativeConsole, this);
    }

    @Override
    default SpigotCommand convertCommand(CommonCommand command) {
        return new SpigotCommand(command);
    }

    @Override
    default Class<Player> getNativePlayerClass() {
        return Player.class;
    }

    @Override
    default Class<ConsoleCommandSender> getNativeConsoleClass() {
        return ConsoleCommandSender.class;
    }


}

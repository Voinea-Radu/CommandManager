package dev.lightdream.commandmanager.spigot.platform;

import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.spigot.command.SpigotCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SpigotAdapter extends Adapter<Player, ConsoleCommandSender, CommandSender> {

    @Override
    public  SpigotPlayer convertPlayer(Player nativePlayer) {
        return new SpigotPlayer(nativePlayer, this);
    }

    @Override
    public  SpigotConsole convertConsole(ConsoleCommandSender nativeConsole) {
        return new SpigotConsole(nativeConsole, this);
    }

    @Override
    public SpigotCommand convertCommand(CommonCommand command) {
        return new SpigotCommand(command);
    }

    @Override
    protected Class<Player> getNativePlayerClass() {
        return Player.class;
    }

    @Override
    protected Class<ConsoleCommandSender> getNativeConsoleClass() {
        return ConsoleCommandSender.class;
    }


}

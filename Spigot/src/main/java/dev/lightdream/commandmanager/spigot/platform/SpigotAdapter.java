package dev.lightdream.commandmanager.spigot.platform;

import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.spigot.command.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SpigotAdapter extends Adapter<Player, CommandSender, ConsoleCommandSender, BaseCommand> {
    @Override
    public PlatformPlayer<Player> convertPlayer(Player player) {
        return new SpigotPlayer(player);
    }

    @Override
    public PlatformCommandSender<CommandSender> convertCommandSender(CommandSender commandSender) {
        return new SpigotCommandSender(commandSender);
    }

    @Override
    public PlatformConsole<ConsoleCommandSender> convertConsole(ConsoleCommandSender consoleCommandSender) {
        return new SpigotConsole(consoleCommandSender);
    }
}

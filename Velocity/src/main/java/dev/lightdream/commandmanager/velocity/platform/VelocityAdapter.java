package dev.lightdream.commandmanager.velocity.platform;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.velocity.command.BaseCommand;

public class VelocityAdapter extends Adapter<Player, CommandSource, ConsoleCommandSource, BaseCommand> {
    @Override
    public PlatformPlayer<Player> convertPlayer(Player player) {
        return new VelocityPlayer(player);
    }

    @Override
    public PlatformCommandSender<CommandSource> convertCommandSender(CommandSource commandSender) {
        return new VelocityCommandSender(commandSender);
    }

    @Override
    public PlatformConsole<ConsoleCommandSource> convertConsole(ConsoleCommandSource consoleCommandSender) {
        return new VelocityConsole(consoleCommandSender);
    }
}

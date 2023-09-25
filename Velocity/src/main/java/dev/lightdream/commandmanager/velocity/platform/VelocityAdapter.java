package dev.lightdream.commandmanager.velocity.platform;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.command.IPlatformCommand;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.velocity.command.VelocityCommand;

public interface VelocityAdapter extends Adapter<Player, ConsoleCommandSource, CommandSource> {
    @Override
    default VelocityPlayer convertPlayer(Player player) {
        return new VelocityPlayer(player, this);
    }

    @Override
    default VelocityConsole convertConsole(ConsoleCommandSource console) {
        return new VelocityConsole(console, this);
    }

    @Override
    default IPlatformCommand convertCommand(CommonCommand command) {
        return new VelocityCommand(command);
    }

    @Override
    default Class<Player> getNativePlayerClass() {
        return Player.class;
    }

    @Override
    default Class<ConsoleCommandSource> getNativeConsoleClass() {
        return ConsoleCommandSource.class;
    }


}

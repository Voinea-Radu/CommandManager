package dev.lightdream.commandmanager.velocity.platform;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.command.IPlatformCommand;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.velocity.command.VelocityCommand;

public class VelocityAdapter extends Adapter<Player, ConsoleCommandSource, CommandSource> {
    @Override
    public VelocityPlayer convertPlayer(Player player) {
        return new VelocityPlayer(player, this);
    }

    @Override
    public  VelocityConsole convertConsole(ConsoleCommandSource console) {
        return new VelocityConsole(console, this);
    }

    @Override
    public IPlatformCommand convertCommand(CommonCommand command) {
        return new VelocityCommand(command);
    }

    @Override
    protected Class<Player> getNativePlayerClass() {
        return Player.class;
    }

    @Override
    protected Class<ConsoleCommandSource> getNativeConsoleClass() {
        return ConsoleCommandSource.class;
    }


}

package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.commandmanager.common.command.CommonBaseCommand;
import dev.lightdream.commandmanager.common.command.ICommonCommand;

public abstract class Adapter<
        Player,
        CommandSender,
        ConsoleCommandSender,
        BaseCommand extends ICommonCommand
        > {

    // From platform to native
    public Player convertPlayer(PlatformPlayer<Player> player) {
        return player.getNativePlayer();
    }

    public CommandSender convertCommandSender(PlatformCommandSender<CommandSender> commandSender) {
        return commandSender.getNativeCommandSender();
    }

    public ConsoleCommandSender convertConsole(PlatformConsole<ConsoleCommandSender> console) {
        return console.getNativeConsole();
    }

    // From native to platform
    public abstract PlatformPlayer<Player> convertPlayer(Player player);

    public abstract PlatformCommandSender<CommandSender> convertCommandSender(CommandSender commandSender);

    public abstract PlatformConsole<ConsoleCommandSender> convertConsole(ConsoleCommandSender player);

    @SuppressWarnings("unchecked")
    public BaseCommand convertCommand(CommonBaseCommand command) {
        return (BaseCommand) command;
    }
}

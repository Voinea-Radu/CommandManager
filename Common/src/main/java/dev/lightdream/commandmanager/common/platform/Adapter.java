package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.commandmanager.common.command.CommonBaseCommand;
import dev.lightdream.commandmanager.common.command.ICommonCommand;

public class Adapter<
        Player,
        CommandSender,
        ConsoleCommandSender,
        BaseCommand extends ICommonCommand
        > {

    // From platform to native
    @SuppressWarnings("unchecked")
    public Player convertPlayer(PlatformPlayer<?> player) {
        return (Player) player.getNativePlayer();
    }

    @SuppressWarnings("unchecked")
    public CommandSender convertCommandSender(PlatformCommandSender commandSender) {
        return (CommandSender) commandSender.object;
    }

    @SuppressWarnings("unchecked")
    public ConsoleCommandSender convertConsole(PlatformConsole console) {
        return (ConsoleCommandSender) console.object;
    }

    // From native to platform
    public PlatformPlayer<?> convertPlayer(Player player) {
        return new PlatformPlayer<>(player);
    }

    public PlatformCommandSender convertCommandSender(CommandSender commandSender) {
        return new PlatformCommandSender(commandSender);
    }

    public PlatformConsole convertConsole(ConsoleCommandSender player) {
        return new PlatformConsole(player);
    }

    @SuppressWarnings("unchecked")
    public BaseCommand convertCommand(CommonBaseCommand command) {
        return (BaseCommand) command;
    }
}

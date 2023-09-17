package dev.lightdream.commandmanager.common.platform;

public class Adapter<
        Player,
        CommandSender,
        ConsoleCommandSender
        > {

    // From platform to native
    @SuppressWarnings("unchecked")
    public Player convertPlayer(PlatformPlayer player) {
        return (Player) player.object;
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
    public PlatformPlayer convertPlayer(Player player) {
        return new PlatformPlayer(player);
    }

    public PlatformCommandSender convertCommandSender(CommandSender commandSender) {
        return new PlatformCommandSender(commandSender);
    }

    public PlatformConsole convertConsole(ConsoleCommandSender player) {
        return new PlatformConsole(player);
    }
}

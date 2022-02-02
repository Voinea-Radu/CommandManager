package dev.lightdream.fly.commands;

import dev.lightdream.fly.CommandMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Executable extends Command {

    public final CommandMain api;

    protected Executable(CommandMain api) {
        super("");
        this.api = api;
    }

    public abstract String getPermission();

    public abstract String getUsage();

    public abstract boolean onlyForPlayers();

    public abstract boolean onlyForConsole();

    public abstract List<String> getAliases();

    public abstract int getMinimumArgs();

    public abstract void execute(CommandSender sender, List<String> args);

    public void _execute(CommandSender sender, List<String> args) {
        if (check(sender, args)) {
            sendUsage(sender);
            return;
        }
        execute(sender, args);
    }

    @SuppressWarnings("unused")
    private boolean check(CommandSender sender, List<String> args) {
        return args.size() < getMinimumArgs();
    }

    public abstract List<String> onTabComplete(CommandSender sender, List<String> args);

    @SuppressWarnings("unused")
    public void sendUsage(CommandSender sender) {
        sender.sendMessage(getUsage());
    }

    public void sendUsage(Player user) {
        user.sendMessage(getUsage());
    }

}

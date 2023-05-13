package dev.lightdream.commandmanager.spigot.command;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.spigot.CommandMain;
import dev.lightdream.logger.Logger;
import dev.lightdream.messagebuilder.MessageBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public abstract class BaseCommand extends org.bukkit.command.Command implements ICommonCommand {

    private List<BaseCommand> subCommands = new ArrayList<>();
    private @Getter boolean enabled = true;

    @SneakyThrows
    public BaseCommand() {
        super("");
        this.init();
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    // Override the default method on org.bukkit.command.Command
    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return getCommandString();
    }

    @Override
    @SneakyThrows
    public final boolean registerCommand() {
        this.setAliases(getAliasList());
        Field fCommandMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
        fCommandMap.setAccessible(true);

        Object commandMapObject = fCommandMap.get(Bukkit.getPluginManager());
        if (commandMapObject instanceof CommandMap) {
            CommandMap commandMap = (CommandMap) commandMapObject;
            commandMap.register(CommonCommandMain.getCommandMain(CommandMain.class).getPlugin().getDescription().getName(), this);
        } else {
            Logger.error("Command " + getCommandString() + " could not be initialized");
            return false;
        }
        Logger.good("Command " + getCommandString() + " initialized successfully");

        return true;
    }

    public void exec(@NotNull CommandSender source, List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + this.getAliasList().get(0) + " for " + source.getName() + ", but the command is not implemented. Exec type: CommandSender, List<String>");
        }

        source.sendMessage(ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    public void exec(@NotNull ConsoleCommandSender console, @NotNull List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + this.getAliasList().get(0) + " for " + console.getName() + ", but the command is not implemented. Exec type: ConsoleCommandSender, List<String>");
        }

        console.sendMessage(ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    public void exec(@NotNull Player player, @NotNull List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + this.getAliasList().get(0) + " for " + player.getName() + ", but the command is not implemented. Exec type: Player, List<String>");
        }

        player.sendMessage(ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    @Override
    public final boolean execute(CommandSender sender, String label, String[] args) {
        if (!isEnabled()) {
            sendMessage(sender, CommonCommandMain.getCommandMain(CommandMain.class).getLang().commandIsDisabled);
            return true;
        }

        if (args.length == 0) {
            distributeExec(sender, new ArrayList<>(Arrays.asList(args)));
            return true;
        }

        for (BaseCommand subCommand : subCommands) {
            if (!(subCommand.getAliasList().contains(args[0].toLowerCase()))) {
                continue;
            }

            if (!checkPermission(sender, subCommand.getPermission())) {
                sender.sendMessage(new MessageBuilder(CommonCommandMain.getCommandMain(CommandMain.class).getLang().noPermission).toString());
                return true;
            }

            subCommand.distributeExec(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            return true;
        }

        distributeExec(sender, new ArrayList<>(Arrays.asList(args)));
        return true;
    }

    public final void distributeExec(CommandSender sender, List<String> args) {
        if (args.size() < getMinimumArgs()) {
            sendUsage(sender);
            return;
        }

        if (onlyForPlayers()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(new MessageBuilder(CommonCommandMain.getCommandMain(CommandMain.class).getLang().onlyFotPlayer).parse());
                return;
            }
            exec((Player) sender, args);
            return;
        }

        if (onlyForConsole()) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(new MessageBuilder(CommonCommandMain.getCommandMain(CommandMain.class).getLang().onlyForConsole).parse());
                return;
            }
            exec((ConsoleCommandSender) sender, args);
            return;
        }

        exec(sender, args);
    }

    @Override
    public final List<String> tabComplete(CommandSender sender, String bukkitAlias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();
            for (BaseCommand subCommand : subCommands) {
                for (String alias : subCommand.getAliasList()) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && checkPermission(sender, subCommand.getPermission())) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }

        for (BaseCommand subCommand : subCommands) {
            if (subCommand.getAliasList().contains(args[0]) && checkPermission(sender, subCommand.getPermission())) {
                return subCommand.onTabComplete(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            }
        }

        return onTabComplete(sender, new ArrayList<>(Arrays.asList(args)));
    }

    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        return Collections.emptyList();
    }

    public final boolean checkPermission(Object sender, String permission) {
        return checkPermission((CommandSender) sender, permission);
    }

    public final boolean checkPermission(CommandSender sender, String permission) {
        return ((sender.hasPermission(permission) || permission.equalsIgnoreCase("")));
    }

    @Override
    public final void sendMessage(Object user, String message) {
        ((CommandSender) user).sendMessage(message);
    }

    @Override
    public final List<ICommonCommand> getSubCommands() {
        return new ArrayList<>(subCommands);
    }

    @Override
    public final void setSubCommands(List<ICommonCommand> subCommands) {
        List<BaseCommand> newSubCommands = new ArrayList<>();

        for (ICommonCommand subCommand : subCommands) {
            newSubCommands.add((BaseCommand) subCommand);
        }

        this.subCommands = newSubCommands;
    }
}

package dev.lightdream.commandmanager.spigot.commands;

import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.command.CommonCommand;
import dev.lightdream.commandmanager.utils.ListUtils;
import dev.lightdream.logger.Logger;
import dev.lightdream.messagebuilder.MessageBuilder;
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

public abstract class BaseCommand extends org.bukkit.command.Command implements CommonCommand {

    public final CommandMain main;
    private List<BaseCommand> subCommands = new ArrayList<>();

    @SneakyThrows
    public BaseCommand(CommandMain main, Object... args) {
        super("");
        this.main = main;
        this.init(args);
    }

    /**
     * Registers the command with Bukkit
     * Do NOT Override this method
     */
    @Override
    @SneakyThrows
    public void registerCommand(Object... args) {
        this.setAliases(getAliases());
        Field fCommandMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
        fCommandMap.setAccessible(true);

        Object commandMapObject = fCommandMap.get(Bukkit.getPluginManager());
        if (commandMapObject instanceof CommandMap commandMap) {
            commandMap.register(getCommand(), this);
        } else {
            Logger.error("Command " + getCommand() + " could not be initialized");
            return;
        }
        Logger.good("Command " + getCommand() + " initialized successfully");
    }

    /**
     * Executes the command
     * You can Override this method
     *
     * @param source The command sender
     * @param args   The arguments
     */
    public void exec(@NotNull CommandSender source, List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + this.getAliases().get(0) + " for " + source.getName() + ", but the command is not implemented. Exec type: CommandSender, List<String>");
        }

        source.sendMessage(ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    /**
     * Executes the command
     * You can Override this method
     *
     * @param console The command sender
     * @param args    The arguments
     */
    public void exec(@NotNull ConsoleCommandSender console, @NotNull List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + this.getAliases().get(0) + " for " + console.getName() + ", but the command is not implemented. Exec type: ConsoleCommandSender, List<String>");
        }

        console.sendMessage(ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    /**
     * Executes the command
     * You can Override this method
     *
     * @param player, The command sender
     * @param args    The arguments
     */
    public void exec(@NotNull Player player, @NotNull List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + this.getAliases().get(0) + " for " + player.getName() + ", but the command is not implemented. Exec type: Player, List<String>");
        }

        player.sendMessage(ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    /**
     * Spigot method to call the command
     * Do NOT Override this method
     *
     * @param sender Source object which is executing this command
     * @param label  The alias of the command used
     * @param args   All arguments passed to the command, split via ' '
     * @return Always true
     */
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            distributeExec(sender, new ArrayList<>(Arrays.asList(args)));
            return true;
        }

        for (BaseCommand subCommand : subCommands) {
            if (!(subCommand.getAliases().contains(args[0].toLowerCase()))) {
                continue;
            }

            if (subCommand.onlyForPlayers() && !(sender instanceof Player)) {
                sender.sendMessage(new MessageBuilder(main.getLang().onlyFotPlayer).toString());
                return true;
            }

            if (subCommand.onlyForConsole() && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(new MessageBuilder(main.getLang().onlyForConsole).toString());
                return true;
            }

            if (!checkPermission(sender, subCommand.getPermission())) {
                sender.sendMessage(new MessageBuilder(main.getLang().noPermission).toString());
                return true;
            }

            subCommand.distributeExec(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            return true;
        }

        distributeExec(sender, new ArrayList<>(Arrays.asList(args)));
        return true;
    }

    /**
     * Distributes the execution to the correct method
     * Do NOT Override this method
     *
     * @param sender The command sender
     * @param args   The arguments
     */
    public void distributeExec(CommandSender sender, List<String> args) {
        if (onlyForPlayers()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(new MessageBuilder(main.getLang().onlyFotPlayer).parse());
                return;
            }
            exec((Player) sender, args);
            return;
        }

        if (onlyForConsole()) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(new MessageBuilder(main.getLang().onlyForConsole).parse());
                return;
            }
            exec((ConsoleCommandSender) sender, args);
            return;
        }

        exec(sender, args);
    }

    /**
     * Completes the command on tab press
     * Do NOT Override this method
     *
     * @param sender      Source object which is executing this command
     * @param bukkitAlias the alias being used
     * @param args        All arguments passed to the command, split via ' '
     * @return A list of possible completions for the final argument
     */
    @Override
    public List<String> tabComplete(CommandSender sender, String bukkitAlias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();
            for (BaseCommand subCommand : subCommands) {
                for (String alias : subCommand.getAliases()) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && checkPermission(sender, subCommand.getPermission())) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }

        for (BaseCommand subCommand : subCommands) {
            if (subCommand.getAliases().contains(args[0]) && checkPermission(sender, subCommand.getPermission())) {
                return subCommand.onTabComplete(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            }
        }

        return onTabComplete(sender, new ArrayList<>(Arrays.asList(args)));
    }

    /**
     * Called when the command is completed on tab press
     * You can Override this method
     *
     * @param sender The command sender
     * @param args   The arguments
     * @return A list of possible completions for the final argument
     */
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        return Collections.emptyList();
    }

    /**
     * Checks if the sender has the permission
     * Do NOT Override this method
     *
     * @param sender     The command sender
     * @param permission The permission
     * @return If the sender has the permission
     */
    public boolean checkPermission(Object sender, String permission) {
        return checkPermission((CommandSender) sender, permission);
    }

    /**
     * Checks if the sender has the permission
     * Do NOT Override this method
     *
     * @param sender     The command sender
     * @param permission The permission
     * @return If the sender has the permission
     */
    public boolean checkPermission(CommandSender sender, String permission) {
        return ((sender.hasPermission(permission) || permission.equalsIgnoreCase("")));
    }

    @Override
    public CommandMain getMain() {
        return main;
    }

    @Override
    public void sendMessage(Object user, String message) {
        ((CommandSender) user).sendMessage(message);
    }

    @Override
    public List<CommonCommand> getSubCommands() {
        return new ArrayList<>(subCommands);
    }

    @Override
    public void saveSubCommands(List<CommonCommand> subCommands) {
        List<BaseCommand> newSubCommands = new ArrayList<>();

        for (CommonCommand subCommand : subCommands) {
            newSubCommands.add((BaseCommand) subCommand);
        }

        this.subCommands = newSubCommands;
    }
}

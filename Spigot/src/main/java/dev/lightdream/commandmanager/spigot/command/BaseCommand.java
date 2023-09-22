package dev.lightdream.commandmanager.spigot.command;

import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.dto.ArgumentList;
import dev.lightdream.commandmanager.spigot.CommandMain;
import dev.lightdream.commandmanager.spigot.platform.SpigotAdapter;
import dev.lightdream.messagebuilder.MessageBuilder;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public abstract class BaseCommand extends SpigotCommonCommandImpl {

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        internalExecute(sender, Arrays.asList(args));
        return true;
    }

    private void internalExecute(CommandSender sender, List<String> arguments) {
        if (!isEnabled()) {
            sendMessage(sender, getMain().getLang().commandIsDisabled);
            return;
        }

        if (!checkPermission(sender, getPermission())) {
            sender.sendMessage(new MessageBuilder(getMain().getLang().noPermission).toString());
            return;
        }

        if (arguments.isEmpty()) {
            distributeExec(sender, arguments);
            return;
        }

        for (ICommonCommand subCommand : getSubCommands()) {
            for (String name : subCommand.getNames()) {
                if (name.equalsIgnoreCase(arguments.get(0))) {
                    BaseCommand baseCommand = (BaseCommand) subCommand;

                    baseCommand.distributeExec(sender, arguments.subList(1, arguments.size()));
                    return;
                }
            }
        }

        distributeExec(sender, arguments);
    }

    private void distributeExec(CommandSender sender, List<String> argumentsList) {
        if (argumentsList.size() != getArguments().size()) {
            sendUsage(sender);
            return;
        }

        ArgumentList arguments = new ArgumentList(argumentsList, this);

        switch (getOnlyFor()) {
            case PLAYER:
                if (!(sender instanceof Player)) {
                    sender.sendMessage(new MessageBuilder(getMain().getLang().onlyFotPlayer).parse());
                    return;
                }
                execute(getAdapter().convertPlayer((Player) sender), arguments);
                break;
            case CONSOLE:
                if (!(sender instanceof ConsoleCommandSender)) {
                    sender.sendMessage(new MessageBuilder(getMain().getLang().onlyForConsole).parse());
                    return;
                }
                execute(getAdapter().convertConsole((ConsoleCommandSender) sender), arguments);
                break;
            case BOTH:
                execute(getAdapter().convertCommandSender(sender), arguments);
                break;
        }
    }

    @Override
    public CommandMain getMain() {
        return (CommandMain) super.getMain();
    }

    protected SpigotAdapter getAdapter() {
        return (SpigotAdapter) getMain().getAdapter();
    }

    @SneakyThrows(value = {IllegalAccessException.class, NoSuchFieldException.class})
    @Override
    public boolean registerCommand(String alias) {
        this.setAliases(getNames());
        Field fCommandMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
        fCommandMap.setAccessible(true);

        Object commandMapObject = fCommandMap.get(Bukkit.getPluginManager());
        if (commandMapObject instanceof CommandMap) {
            CommandMap commandMap = (CommandMap) commandMapObject;
            commandMap.register(getMain().getPlugin().getDescription().getName(), this);
            return true;
        }

        return false;
    }

    @Override
    public void sendMessage(Object user, String message) {
        ((CommandSender) user).sendMessage(message);
    }

    @Override
    public boolean checkPermission(Object user, String permission) {
        return ((CommandSender) user).hasPermission(permission);
    }

    @Override
    public final List<String> tabComplete(CommandSender sender, String bukkitAlias, String[] args) throws IllegalArgumentException {
        return tabComplete(sender, Arrays.asList(args));
    }

    public final List<String> tabComplete(CommandSender sender, List<String> arguments) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;

        if (arguments.size() == 1) {
            ArrayList<String> output = new ArrayList<>();

            for (ICommonCommand subCommand : getSubCommands()) {
                for (String name : subCommand.getNames()) {
                    if (name.toLowerCase().contains(arguments.get(0).toLowerCase())) {
                        output.add(name);
                    }
                }
            }

            return output;
        }

        for (ICommonCommand subCommand : getSubCommands()) {
            for (String name : subCommand.getNames()) {
                if (name.equalsIgnoreCase(arguments.get(0))) {
                    if (!checkPermission(sender, subCommand.getPermission())) {
                        continue;
                    }

                    BaseCommand baseCommand = (BaseCommand) subCommand;

                    return baseCommand.tabComplete(sender, new ArrayList<>(arguments.subList(1, arguments.size())));
                }
            }
        }

        String argument = getArguments().get((arguments.size() - 1) % getArguments().size());

        return suggest(getAdapter().convertPlayer(player), argument);
    }
}

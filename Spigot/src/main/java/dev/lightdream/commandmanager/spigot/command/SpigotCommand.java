package dev.lightdream.commandmanager.spigot.command;

import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.command.ICommand;
import dev.lightdream.commandmanager.common.command.IPlatformCommand;
import dev.lightdream.commandmanager.common.dto.ArgumentList;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.spigot.CommandMain;
import dev.lightdream.commandmanager.spigot.platform.SpigotAdapter;
import dev.lightdream.commandmanager.spigot.platform.SpigotConsole;
import dev.lightdream.commandmanager.spigot.platform.SpigotPlayer;
import dev.lightdream.messagebuilder.MessageBuilder;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class SpigotCommand extends org.bukkit.command.Command implements IPlatformCommand {

    private CommonCommand commonCommand;

    public SpigotCommand(CommonCommand commonCommand) {
        super("");
        this.commonCommand = commonCommand;
    }

    @Override
    public CommonCommand getCommonCommand() {
        return commonCommand;
    }

    @Override
    public void setCommonCommand(CommonCommand commonCommand) {
        this.commonCommand = commonCommand;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        internalExecute(getAdapter().convertCommandSender(sender), Arrays.asList(args));
        return true;
    }

    private void internalExecute(PlatformCommandSender sender, List<String> arguments) {
        if (!isEnabled()) {
            sender.sendMessage(getMain().getLang().commandIsDisabled);
            return;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(new MessageBuilder(getMain().getLang().noPermission).toString());
            return;
        }

        if (arguments.isEmpty()) {
            distributeExec(sender, arguments);
            return;
        }

        for (ICommand subCommand : getSubCommands()) {
            for (String name : subCommand.getNames()) {
                if (name.equalsIgnoreCase(arguments.get(0))) {
                    SpigotCommand baseCommand = (SpigotCommand) subCommand;

                    baseCommand.distributeExec(sender, arguments.subList(1, arguments.size()));
                    return;
                }
            }
        }

        distributeExec(sender, arguments);
    }

    private void distributeExec(PlatformCommandSender sender, List<String> argumentsList) {
        if (argumentsList.size() != getArguments().size()) {
            sendUsage(sender);
            return;
        }

        ArgumentList arguments = new ArgumentList(argumentsList, commonCommand);

        switch (getOnlyFor()) {
            case PLAYER:
                if (!(sender instanceof SpigotPlayer)) {
                    sender.sendMessage(new MessageBuilder(getMain().getLang().onlyFotPlayer).parse());
                    return;
                }
                execute((SpigotPlayer) sender, arguments);
                break;
            case CONSOLE:
                if (!(sender instanceof SpigotConsole)) {
                    sender.sendMessage(new MessageBuilder(getMain().getLang().onlyForConsole).parse());
                    return;
                }
                execute((SpigotConsole) sender, arguments);
                break;
            case BOTH:
                execute(sender, arguments);
                break;
        }
    }

    @Override
    public CommandMain getMain() {
        return (CommandMain) IPlatformCommand.super.getMain();
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
    public final List<String> tabComplete(CommandSender sender, String bukkitAlias, String[] args) throws IllegalArgumentException {
        return tabComplete(sender, Arrays.asList(args));
    }

    public final List<String> tabComplete(CommandSender sender, List<String> arguments) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player nativePlayer = (Player) sender;
        SpigotPlayer platformPlayer = getAdapter().convertPlayer(nativePlayer);

        if (arguments.size() == 1) {
            ArrayList<String> output = new ArrayList<>();

            for (ICommand subCommand : getSubCommands()) {
                for (String name : subCommand.getNames()) {
                    if (name.toLowerCase().contains(arguments.get(0).toLowerCase())) {
                        output.add(name);
                    }
                }
            }

            return output;
        }

        for (ICommand subCommand : getSubCommands()) {
            for (String name : subCommand.getNames()) {
                if (name.equalsIgnoreCase(arguments.get(0))) {
                    if (!platformPlayer.hasPermission(subCommand.getPermission())) {
                        continue;
                    }

                    SpigotCommand baseCommand = (SpigotCommand) subCommand;

                    return baseCommand.tabComplete(sender, new ArrayList<>(arguments.subList(1, arguments.size())));
                }
            }
        }

        String argument = getArguments().get((arguments.size() - 1) % getArguments().size());

        return suggest(platformPlayer, argument);
    }
}

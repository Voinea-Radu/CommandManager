package dev.lightdream.commandmanager.velocity.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.command.ICommand;
import dev.lightdream.commandmanager.common.command.IPlatformCommand;
import dev.lightdream.commandmanager.common.dto.ArgumentList;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.velocity.CommandMain;
import dev.lightdream.commandmanager.velocity.platform.VelocityConsole;
import dev.lightdream.commandmanager.velocity.platform.VelocityPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("unused")
public class VelocityCommand implements SimpleCommand, IPlatformCommand {

    private final CommonCommand commonCommand;

    public VelocityCommand(CommonCommand commonCommand) {
        this.commonCommand = commonCommand;
    }

    @Override
    public CommonCommand getCommonCommand() {
        return commonCommand;
    }


    @Override
    public void execute(Invocation invocation) {
        PlatformCommandSender sender = getMain().getAdapter().convertCommandSender(invocation.source());
        List<String> arguments = Arrays.asList(invocation.arguments());

        if (!isEnabled()) {
            sender.sendMessage(getMain().getLang().commandIsDisabled);
            return;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMain().getLang().noPermission);
            return;
        }

        if (arguments.isEmpty()) {
            distributeExecute(sender, arguments);
            return;
        }

        for (ICommand subCommand : getSubCommands()) {
            for (String name : subCommand.getNames()) {
                if (name.equalsIgnoreCase(arguments.get(0))) {
                    VelocityCommand baseCommand = (VelocityCommand) subCommand;

                    baseCommand.distributeExecute(sender, arguments.subList(1, arguments.size()));
                    return;
                }
            }
        }

        distributeExecute(sender, arguments);
    }

    private void distributeExecute(PlatformCommandSender sender, List<String> argumentsList) {
        if (!isEnabled()) {
            sender.sendMessage(getMain().getLang().commandIsDisabled);
            return;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMain().getLang().noPermission);
            return;
        }

        ArgumentList arguments = new ArgumentList(argumentsList, getCommonCommand());

        if (onlyForConsole()) {
            if (!(sender instanceof VelocityPlayer)) {
                sender.sendMessage(getMain().getLang().onlyForConsole);
                return;
            }
            execute((VelocityPlayer) sender, arguments);
            return;
        }
        if (onlyForPlayers()) {
            if (!(sender instanceof VelocityConsole)) {
                sender.sendMessage(getMain().getLang().onlyFotPlayer);
                return;
            }
            execute((VelocityConsole) sender, arguments);
            return;
        }

        execute(sender, arguments);
    }

    @Override
    public final boolean registerCommand(String name) {
        CommandManager commandManager = getMain().getProxy().getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder(name)
                .plugin(this)
                .build();

        commandManager.register(commandMeta, this);
        return true;
    }


    @Override
    public CommandMain getMain() {
        return (CommandMain) IPlatformCommand.super.getMain();
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return tabComplete(invocation.source(), Arrays.asList(invocation.arguments()));
    }

    private List<String> tabComplete(CommandSource nativeSource, List<String> arguments) {
        if (!(nativeSource instanceof Player)) {
            return new ArrayList<>();
        }

        Player nativePlayer = (Player) nativeSource;
        VelocityPlayer player = getMain().getAdapter().convertPlayer(nativePlayer);

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
                    if (!player.hasPermission(subCommand.getPermission())) {
                        continue;
                    }

                    VelocityCommand baseCommand = (VelocityCommand) subCommand;

                    return baseCommand.tabComplete(nativeSource, new ArrayList<>(arguments.subList(1, arguments.size())));
                }
            }
        }

        String argument = getArguments().get((arguments.size() - 1) % getArguments().size());

        return suggest(player, argument);
    }
}
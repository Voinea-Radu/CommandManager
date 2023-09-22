package dev.lightdream.commandmanager.velocity.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.lightdream.commandmanager.common.command.CommonCommandImpl;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.dto.ArgumentList;
import dev.lightdream.commandmanager.velocity.CommandMain;
import dev.lightdream.commandmanager.velocity.platform.VelocityAdapter;
import dev.lightdream.messagebuilder.MessageBuilder;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("unused")
public abstract class BaseCommand extends CommonCommandImpl implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        List<String> arguments = Arrays.asList(invocation.arguments());

        if (!isEnabled()) {
            sendMessage(sender, getMain().getLang().commandIsDisabled);
            return;
        }

        if (!checkPermission(sender, getPermission())) {
            sender.sendMessage(Component.text(new MessageBuilder(getMain().getLang().noPermission).toString()));
            return;
        }

        if (arguments.isEmpty()) {
            distributeExecute(sender, arguments);
            return;
        }

        for (ICommonCommand subCommand : getSubCommands()) {
            for (String name : subCommand.getNames()) {
                if (name.equalsIgnoreCase(arguments.get(0))) {
                    BaseCommand baseCommand = (BaseCommand) subCommand;

                    baseCommand.distributeExecute(sender, arguments.subList(1, arguments.size()));
                    return;
                }
            }
        }

        distributeExecute(sender, arguments);
    }

    private void distributeExecute(CommandSource sender, List<String> argumentsList) {
        if (!isEnabled()) {
            sendMessage(sender, getMain().getLang().commandIsDisabled);
            return;
        }

        if (!checkPermission(sender, getPermission())) {
            sendMessage(sender, getMain().getLang().noPermission);
            return;
        }

        ArgumentList arguments = new ArgumentList(argumentsList, this);

        if (onlyForConsole()) {
            if (!(sender instanceof ConsoleCommandSource)) {
                sender.sendMessage(Component.text(getMain().getLang().onlyForConsole));
                return;
            }
            execute(getAdapter().convertConsole((ConsoleCommandSource) sender), arguments);
            return;
        }
        if (onlyForPlayers()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text(getMain().getLang().onlyFotPlayer));
                return;
            }
            execute(getAdapter().convertPlayer((Player) sender), arguments);
            return;
        }

        execute(getAdapter().convertCommandSender(sender), arguments);
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
    public final void sendMessage(Object user, String message) {
        CommandSource source = (CommandSource) user;
        source.sendMessage(Component.text(message));
    }

    @Override
    public final boolean checkPermission(Object user, String permission) {
        CommandSource source = (CommandSource) user;
        return source.hasPermission(permission);
    }

    @Override
    public CommandMain getMain() {
        return (CommandMain) super.getMain();
    }

    protected VelocityAdapter getAdapter() {
        return (VelocityAdapter) getMain().getAdapter();
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return tabComplete(invocation.source(), Arrays.asList(invocation.arguments()));
    }

    private List<String> tabComplete(CommandSource sender, List<String> arguments) {
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
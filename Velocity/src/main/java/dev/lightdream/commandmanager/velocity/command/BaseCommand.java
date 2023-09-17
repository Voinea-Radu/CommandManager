package dev.lightdream.commandmanager.velocity.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.lightdream.commandmanager.common.command.CommonCommandImpl;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.velocity.CommandMain;
import dev.lightdream.logger.Debugger;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("unused")
public abstract class BaseCommand extends CommonCommandImpl implements SimpleCommand {

    @Override
    public List<String> suggest(Invocation invocation) {
        int argsLength = invocation.arguments().length;

        if (argsLength == 0) {
            return onAutoComplete(invocation);
        }

        String lastArg1 = invocation.arguments()[argsLength - 1];
        BaseCommand subCommand = getSubCommand(lastArg1);

        if (subCommand == null) {
            if (argsLength == 1) {
                return ListUtils.getListThatStartsWith(onAutoComplete(invocation), lastArg1);
            }

            String lastArg2 = invocation.arguments()[argsLength - 2];
            subCommand = getSubCommand(lastArg2);

            if (subCommand == null) {
                return ListUtils.getListThatStartsWith(onAutoComplete(invocation), lastArg2);
            }

            List<String> a = subCommand.onAutoComplete(invocation);

            Debugger.log(a);

            return ListUtils.getListThatStartsWith(a, lastArg1);
        }

        return ListUtils.getListThatStartsWith(onAutoComplete(invocation), lastArg1);
    }

    private @Nullable BaseCommand getSubCommand(String name) {
        for (ICommonCommand subCommand : getSubCommands()) {
            for (String commandName : subCommand.getNames()) {
                if (name.equalsIgnoreCase(commandName)) {
                    return (BaseCommand) subCommand;
                }
            }
        }

        return null;
    }

    public @NotNull List<String> onAutoComplete(Invocation invocation) {
        return defaultAutoComplete();
    }

    public @NotNull List<String> defaultAutoComplete() {
        List<String> allArguments = new ArrayList<>();

        for (ICommonCommand subCommand : getSubCommands()) {
            allArguments.add(subCommand.getName());
        }

        return allArguments;
    }

    @Override
    public final boolean registerCommand(String name) {
        CommandManager commandManager = ((CommandMain) getMain()).getProxy().getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder(name)
                .plugin(this)
                .build();

        commandManager.register(commandMeta, this);
        return true;
    }

    @Override
    public final void execute(Invocation invocation) {
        distributeExecute(invocation.source(), Arrays.asList(invocation.arguments()));
    }

    private void distributeExecute(CommandSource source, List<String> args) {

        if (!isEnabled()) {
            sendMessage(source, getMain().getLang().commandIsDisabled);
            return;
        }

        if (!checkPermission(source, getPermission())) {
            sendMessage(source, getMain().getLang().noPermission);
            return;
        }

        if (!args.isEmpty()) {
            for (ICommonCommand subCommand : getSubCommands()) {
                boolean found = false;

                for (String name : subCommand.getNames()) {
                    if (name.equalsIgnoreCase(args.get(0))) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    continue;
                }

                BaseCommand baseCommand = (BaseCommand) subCommand;

                baseCommand.distributeExecute(source, args.subList(1, args.size()));
                return;
            }
        }

        if (args.size() < getMinimumArgs()) {
            sendUsage(source);
            return;
        }

        if (onlyForConsole()) {
            if (!(source instanceof ConsoleCommandSource)) {
                source.sendMessage(Component.text(getMain().getLang().onlyForConsole));
                return;
            }
            ConsoleCommandSource consoleSource = (ConsoleCommandSource) source;
            exec(new PlatformConsole(consoleSource), args);
            return;
        }
        if (onlyForPlayers()) {
            if (!(source instanceof Player)) {
                source.sendMessage(Component.text(getMain().getLang().onlyFotPlayer));
                return;
            }
            Player player = (Player) source;
            exec(new PlatformPlayer(player), args);
            return;
        }

        exec(new PlatformCommandSender(source), args);
    }

    @Override
    public final boolean checkPermission(Object user, String permission) {
        CommandSource source = (CommandSource) user;
        return source.hasPermission(permission);
    }

    @Override
    public final void sendMessage(Object user, String message) {
        CommandSource source = (CommandSource) user;
        source.sendMessage(Component.text(message));
    }

}
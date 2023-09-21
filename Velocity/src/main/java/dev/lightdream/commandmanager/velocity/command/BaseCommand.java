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
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.velocity.CommandMain;
import dev.lightdream.commandmanager.velocity.platform.VelocityAdapter;
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

    private void distributeExecute(CommandSource sender, List<String> argumentsList) {
        if (!isEnabled()) {
            sendMessage(sender, getMain().getLang().commandIsDisabled);
            return;
        }

        if (!checkPermission(sender, getPermission())) {
            sendMessage(sender, getMain().getLang().noPermission);
            return;
        }

        if (!argumentsList.isEmpty()) {
            for (ICommonCommand subCommand : getSubCommands()) {
                boolean found = false;

                for (String name : subCommand.getNames()) {
                    if (name.equalsIgnoreCase(argumentsList.get(0))) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    continue;
                }

                BaseCommand baseCommand = (BaseCommand) subCommand;

                baseCommand.distributeExecute(sender, argumentsList.subList(1, argumentsList.size()));
                return;
            }
        }

        if (argumentsList.size() < getMinimumArgs()) {
            sendUsage(sender);
            return;
        }

        ArgumentList arguments = new ArgumentList(argumentsList, this);

        if (onlyForConsole()) {
            if (!(sender instanceof ConsoleCommandSource)) {
                sender.sendMessage(Component.text(getMain().getLang().onlyForConsole));
                return;
            }
            exec(getAdapter().convertConsole((ConsoleCommandSource) sender), arguments);
            return;
        }
        if (onlyForPlayers()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text(getMain().getLang().onlyFotPlayer));
                return;
            }
            exec(getAdapter().convertPlayer((Player) sender), arguments);
            return;
        }

        exec(getAdapter().convertCommandSender(sender), arguments);
    }

    @Override
    public CommandMain getMain() {
        return (CommandMain) super.getMain();
    }

    protected VelocityAdapter getAdapter() {
        return (VelocityAdapter) getMain().getAdapter();
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
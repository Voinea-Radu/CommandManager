package dev.lightdream.commandmanager.velocity.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.velocity.CommandMain;
import dev.lightdream.logger.Logger;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings("unused")
public abstract class BaseCommand implements CommonCommand, SimpleCommand {

    public List<CommonCommand> subCommands = new ArrayList<>();

    public BaseCommand() {
        this.init();
    }

    @Override
    public final void registerCommand() {
        CommandManager commandManager = CommonCommandMain.getCommandMain(CommandMain.class).getProxy().getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder(getAliasList().get(0))
                .aliases(getAliasList().subList(1, getAliasList().size()).toArray(new String[0]))
                .plugin(this)
                .build();

        commandManager.register(commandMeta, this);
        Logger.good("Command " + getCommand() + " initialized successfully");
    }

    @Override
    public final void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        List<String> args = Arrays.stream(invocation.arguments()).collect(Collectors.toList());

        if (args.size() < getMinimumArgs()) {
            sendUsage(source);
            return;
        }

        if (onlyForConsole()) {
            if (!(source instanceof ConsoleCommandSource)) {
                source.sendMessage(Component.text(CommonCommandMain.getCommandMain(CommandMain.class).getLang().onlyForConsole));
                return;
            }
            ConsoleCommandSource consoleSource = (ConsoleCommandSource) source;
            exec(consoleSource, args);
            return;
        }
        if (onlyForPlayers()) {
            if (!(source instanceof Player)) {
                source.sendMessage(Component.text(CommonCommandMain.getCommandMain(CommandMain.class).getLang().onlyFotPlayer));
                return;
            }
            Player player = (Player) source;
            exec(player, args);
            return;
        }

        exec(source, args);
    }

    /**
     * Executes the command
     *
     * @param source The commander who is executing this command
     * @param args   The parsed command arguments for this command
     */
    public void exec(@NotNull CommandSource source, @NotNull List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommand() + " for " + source + ", but the command is not implemented. Exec type: CommandSource, CommandContext");
        }

        source.sendMessage(Component.text(ListUtils.listToString(getSubCommandsHelpMessage(), "\n")));
    }

    /**
     * Executes the command
     *
     * @param console The commander who is executing this command
     * @param args    The parsed command arguments for this command
     */
    public void exec(@NotNull ConsoleCommandSource console, @NotNull List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommand() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        console.sendMessage(Component.text(ListUtils.listToString(getSubCommandsHelpMessage(), "\n")));
    }

    /**
     * Executes the command
     *
     * @param player The commander who is executing this command
     * @param args   The parsed command arguments for this command
     */
    public void exec(@NotNull Player player, @NotNull List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommand() + " for " + player.getUsername() + ", but the command is not implemented. Exec type: User, CommandContext");
        }

        player.sendMessage(Component.text(ListUtils.listToString(getSubCommandsHelpMessage(), "\n")));
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

    @Override
    public final List<CommonCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public final void saveSubCommands(List<CommonCommand> subCommands) {
        this.subCommands = subCommands;
    }
}
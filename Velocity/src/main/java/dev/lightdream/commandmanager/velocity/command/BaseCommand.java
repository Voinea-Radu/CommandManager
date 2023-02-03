package dev.lightdream.commandmanager.velocity.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.lightdream.commandmanager.common.CommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.utils.ListUtils;
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

    private final CommandMain main;
    public List<CommonCommand> subCommands = new ArrayList<>();
    @Getter
    @Setter
    private Command commandAnnotation;

    public BaseCommand(CommandMain main, Object... args) {
        this.main = main;
        this.init(args);
    }

    @Override
    public final void registerCommand(Object... args) {
        ProxyServer proxy = (ProxyServer) args[0];

        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder(getAliases().get(0))
                .aliases(getAliases().subList(1, getAliases().size()).toArray(new String[0]))
                .plugin(this)
                .build();

        commandManager.register(commandMeta, this);
    }

    @Override
    public final void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        List<String> args = Arrays.stream(invocation.arguments()).collect(Collectors.toList());

        if (onlyForConsole()) {
            if (!(source instanceof ConsoleCommandSource)) {
                source.sendMessage(Component.text(main.getLang().onlyForConsole));
                return;
            }
            ConsoleCommandSource consoleSource = (ConsoleCommandSource) source;
            exec(consoleSource, args);
            return;
        }
        if (onlyForPlayers()) {
            if (!(source instanceof Player)) {
                source.sendMessage(Component.text(main.getLang().onlyFotPlayer));
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
    public final CommandMain getMain() {
        return main;
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
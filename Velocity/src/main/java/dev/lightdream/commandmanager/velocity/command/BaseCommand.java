package dev.lightdream.commandmanager.velocity.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.command.CommonCommand;
import dev.lightdream.commandmanager.utils.ListUtils;
import dev.lightdream.logger.Logger;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseCommand implements CommonCommand, SimpleCommand {

    private final CommandMain main;
    public List<CommonCommand> subCommands = new ArrayList<>();

    /**
     * @param main The main class instance
     * @param args ProxyServer instance
     */
    public BaseCommand(CommandMain main, ProxyServer proxy) {
        this.main = main;
        this.init(proxy);
    }

    /**
     * @param args ProxyServer instance
     */
    @Override
    public void registerCommand(Object... args) {
        ProxyServer proxy = (ProxyServer) args[0];

        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder(getAliases().get(0))
                .aliases(getAliases().subList(1, getAliases().size()).toArray(new String[0]))
                .plugin(this)
                .build();

        commandManager.register(commandMeta, this);
    }

    /**
     * Sponge API call to execute the command
     *
     * @param invocation The command invocation (containing the source and arguments)
     */
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        List<String> args = new ArrayList<>(List.of(invocation.arguments()));

        if (onlyForConsole()) {
            if (!(source instanceof ConsoleCommandSource consoleSource)) {
                source.sendMessage(Component.text(main.getLang().onlyForConsole));
                return;
            }
            exec(consoleSource, args);
            return;
        }
        if (onlyForPlayers()) {
            if (!(source instanceof Player player)) {
                source.sendMessage(Component.text(main.getLang().onlyFotPlayer));
                return;
            }
            exec(player, args);
            return;
        }

        exec(source, args);
    }

    /**
     * Executes the command
     * You can Override this method
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
     * You can Override this method
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
     * You can Override this method
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
    public boolean checkPermission(Object user, String permission) {
        CommandSource source = (CommandSource) user;
        return source.hasPermission(permission);
    }

    @Override
    public void sendMessage(Object user, String message) {
        CommandSource source = (CommandSource) user;
        source.sendMessage(Component.text(message));
    }

    @Override
    public CommandMain getMain() {
        return main;
    }

    @Override
    public List<CommonCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public void saveSubCommands(List<CommonCommand> subCommands) {
        this.subCommands = subCommands;
    }
}
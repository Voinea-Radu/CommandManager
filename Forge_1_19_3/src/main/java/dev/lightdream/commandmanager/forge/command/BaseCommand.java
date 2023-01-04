package dev.lightdream.commandmanager.forge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.command.CommonCommand;
import dev.lightdream.commandmanager.forge.util.PermissionUtil;
import dev.lightdream.commandmanager.utils.ListUtils;
import dev.lightdream.logger.Logger;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements CommonCommand {

    private final CommandMain main;
    private final boolean runAsync = false;
    public List<String> aliases;
    private List<CommonCommand> subCommands = new ArrayList<>();

    /**
     * @param main The main class instance
     */
    public BaseCommand(CommandMain main, CommandDispatcher<CommandSourceStack> dispatcher) {
        this.main = main;
        this.init(dispatcher);
    }

    @Override
    public CommandMain getMain() {
        return main;
    }

    /**
     * @param args CommandDispatcher<CommandSourceStack> instance
     */
    @Override
    public void registerCommand(Object... args) {
        if (args.length == 0) {
            Logger.error("No CommandDispatcher was passed to the register method!");
            return;
        }
        //noinspection unchecked
        CommandDispatcher<CommandSourceStack> dispatcher = (CommandDispatcher<CommandSourceStack>) args[0];
        dispatcher.register(getCommandBuilder());
    }

    /**
     * Generated a command builder
     *
     * @return The command builder
     */
    private LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder() {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal(getCommand());

        List<CommonCommand> subCommands = getSubCommands();
        List<RequiredArgumentBuilder<CommandSourceStack, ?>> arguments = getArguments();

        if (subCommands == null) {
            subCommands = new ArrayList<>();
        }
        if (arguments == null) {
            arguments = new ArrayList<>();
        }

        for (CommonCommand subCommandObject : subCommands) {
            BaseCommand subCommand = (BaseCommand) subCommandObject;
            command.then(subCommand.getCommandBuilder());
        }

        for (RequiredArgumentBuilder<CommandSourceStack, ?> argument : arguments) {
            command.then(argument);
        }

        command.executes(this::internalExecute);

        return command;
    }

    /**
     * API call to execute the command
     *
     * @param context The command context
     * @return 0
     */
    private int internalExecute(CommandContext<CommandSourceStack> context) {
        CommandSource source = context.getSource().source;

        if (onlyForConsole()) {
            if (!(source instanceof MinecraftServer consoleSource)) {
                sendMessage(source, main.getLang().onlyForConsole);
                return 0;
            }
            exec(consoleSource, context);
            return 0;
        }
        if (onlyForPlayers()) {
            if (!(source instanceof Player player)) {
                sendMessage(source, main.getLang().onlyFotPlayer);
                return 0;
            }
            exec(player, context);
            return 0;
        }

        exec(source, context);
        return 0;
    }

    /**
     * Executes the command
     * You can Override this method
     *
     * @param source  The commander who is executing this command
     * @param context The parsed command context
     */
    public void exec(@NotNull CommandSource source, @NotNull CommandContext<CommandSourceStack> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommand() + " for " + source + ", but the command is not implemented. Exec type: CommandSource, CommandContext");
        }

        sendMessage(source, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    /**
     * Executes the command
     * You can Override this method
     *
     * @param console The commander who is executing this command
     * @param context The parsed command context
     */
    public void exec(@NotNull MinecraftServer console, @NotNull CommandContext<CommandSourceStack> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommand() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        sendMessage(console, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    /**
     * Executes the command
     * You can Override this method
     *
     * @param player  The commander who is executing this command
     * @param context The parsed command context
     */
    public void exec(@NotNull Player player, @NotNull CommandContext<CommandSourceStack> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommand() + " for " + player.getName() + ", but the command is not implemented. Exec type: User, CommandContext");
        }

        sendMessage(player, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    /**
     * Get the command arguments
     * You can Override this method
     *
     * @return The command arguments
     */
    public @Nullable List<RequiredArgumentBuilder<CommandSourceStack, ?>> getArguments() {
        return new ArrayList<>();
    }

    @Override
    public boolean checkPermission(Object user, String permission) {
        return PermissionUtil.checkPermission((Player) user, permission);
    }

    @Override
    public void sendMessage(Object user, String message) {
        if (user instanceof Player player) {
            player.displayClientMessage(Component.literal(message), false);
            return;
        }
        if (user instanceof MinecraftServer) {
            Logger.info(message);
            return;
        }
        Logger.info("Message sent to " + user + ": " + message);
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
package dev.lightdream.commandmanager.forge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.common.CommandMain;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.forge.util.PermissionUtil;
import dev.lightdream.commandmanager.common.utils.ListUtils;
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

@SuppressWarnings("unused")
public abstract class BaseCommand implements CommonCommand {

    private final CommandMain main;
    private List<CommonCommand> subCommands = new ArrayList<>();

    public BaseCommand(CommandMain main, CommandDispatcher<CommandSourceStack> dispatcher) {
        this.main = main;
        this.init(dispatcher);
    }

    @Override
    public final CommandMain getMain() {
        return main;
    }

    @Override
    public final void registerCommand(Object... args) {
        if (args.length == 0) {
            Logger.error("No CommandDispatcher was passed to the register method!");
            return;
        }
        //noinspection unchecked
        CommandDispatcher<CommandSourceStack> dispatcher = (CommandDispatcher<CommandSourceStack>) args[0];
        dispatcher.register(getCommandBuilder());
    }

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
     *
     * @return The command arguments
     */
    public @Nullable List<RequiredArgumentBuilder<CommandSourceStack, ?>> getArguments() {
        return new ArrayList<>();
    }

    @Override
    public final boolean checkPermission(Object user, String permission) {
        return PermissionUtil.checkPermission((Player) user, permission);
    }

    @Override
    public final void sendMessage(Object user, String message) {
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
    public final List<CommonCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public final void saveSubCommands(List<CommonCommand> subCommands) {
        this.subCommands = subCommands;
    }
}
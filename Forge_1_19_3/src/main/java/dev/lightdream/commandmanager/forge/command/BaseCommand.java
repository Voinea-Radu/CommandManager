package dev.lightdream.commandmanager.forge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.command.CommonCommand;
import dev.lightdream.commandmanager.forge.util.PermissionUtil;
import dev.lightdream.logger.Logger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements CommonCommand {

    private final CommandMain main;
    private final boolean runAsync = false;
    public List<String> aliases;

    /**
     * @param main The main class instance
     * @param args CommandDispatcher<CommandSourceStack> instance
     */
    public BaseCommand(CommandMain main, Object... args) {
        this.main = main;
        this.init(args);
    }

    /**
     * @param args CommandDispatcher<CommandSourceStack> instance
     */
    @Override
    public void registerCommand(Object... args) {
        if(args.length==0){
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
        execute(context);
        return 0;
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

    /**
     * Get the sub commands
     *
     * @param context The command context
     */
    public abstract void execute(CommandContext<CommandSourceStack> context);

    @Override
    public boolean checkPermission(Object user, String permission) {
        return PermissionUtil.checkPermission((Player) user, permission);
    }

    @Override
    public void sendMessage(Object user, String message) {
        Player player = (Player) user;
        player.displayClientMessage(Component.literal(message), false);
    }

    @Override
    public void setPermission(String permission) {
        // noop
    }

}
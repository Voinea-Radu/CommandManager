package dev.lightdream.commandmanager.forge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.command.CommonCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements CommonCommand {

    private final CommandMain main;
    public List<String> aliases;
    private final boolean runAsync = false;

    public BaseCommand(CommandMain main) {
        this.main = main;
        this.init();
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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

}
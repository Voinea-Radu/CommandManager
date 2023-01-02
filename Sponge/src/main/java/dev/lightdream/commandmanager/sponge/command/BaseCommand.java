package dev.lightdream.commandmanager.sponge.command;

import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.annotation.Command;
import dev.lightdream.commandmanager.command.CommonCommand;
import dev.lightdream.commandmanager.sponge.dto.CommandSpecWrap;
import dev.lightdream.lambda.ScheduleUtils;
import dev.lightdream.lambda.lambda.LambdaExecutor;
import dev.lightdream.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, CommonCommand {

    private final CommandMain main;
    public CommandSpecWrap spec;
    public List<String> aliases;
    private boolean runAsync = false;

    public BaseCommand(CommandMain main, Object... args) {
        this.main = main;
        this.init(args);
    }

    @Override
    public void registerCommand(Object... args) {
        this.spec = CommandSpecWrap.builder().build();

        Command command = getClass().getAnnotation(Command.class);
        runAsync = command.async();

        String permission = command.permission();
        if (!permission.equals("")) {
            spec.spec.permission(permission);
        }
        spec.onlyForConsole = command.onlyForConsole();
        spec.onlyForPlayers = command.onlyForPlayers();

        Sponge.getCommandManager().register(main, getCommandSpec(), command.aliases());
    }

    /**
     * Gets the command specification
     *
     * @return The command specification
     */
    public CommandSpec getCommandSpec() {
        getSubCommands().forEach(commandObject -> {
            BaseCommand command = (BaseCommand) commandObject;
            spec.spec.child(command.getCommandSpec(), command.aliases);
        });
        spec.spec.executor(this);
        spec.spec.arguments(getArgs().toArray(new CommandElement[0]));
        return spec.spec.build();
    }

    /**
     * Gets the command string
     * You can Override this method
     *
     * @return The command string
     */
    public List<CommandElement> getArgs() {
        return new ArrayList<>();
    }

    /**
     * Sponge API call to execute the command
     *
     * @param src  The commander who is executing this command
     * @param args The parsed command arguments for this command
     * @return The command result
     */
    @Override
    public final @NotNull CommandResult execute(@NotNull CommandSource src, @NotNull CommandContext args) {
        LambdaExecutor executor = () -> {
            if (spec.onlyForConsole) {
                if (!(src instanceof ConsoleSource)) {
                    src.sendMessage(Text.of(main.getLang().onlyForConsole));
                    return;
                }
                exec((ConsoleSource) src, args);
                return;
            }
            if (spec.onlyForPlayers) {
                if (!(src instanceof Player player)) {
                    src.sendMessage(Text.of(main.getLang().onlyFotPlayer));
                    return;
                }
                exec(player, args);
                return;
            }

            exec(src, args);
        };

        if (runAsync) {
            ScheduleUtils.runTaskAsync(executor);
        } else {
            executor.execute();
        }

        return CommandResult.success();
    }

    /**
     * Executes the command
     * You can Override this method
     *
     * @param source The commander who is executing this command
     * @param args   The parsed command arguments for this command
     */
    public void exec(@NotNull CommandSource source, @NotNull CommandContext args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + args.createSnapshot() + " for " + source.getName() + ", but the command is not implemented. Exec type: CommandSource, CommandContext");
        }

        source.sendMessages(Text.of(getSubCommandsHelpMessage()));
    }

    /**
     * Executes the command
     * You can Override this method
     *
     * @param console The commander who is executing this command
     * @param args    The parsed command arguments for this command
     */
    public void exec(@NotNull ConsoleSource console, @NotNull CommandContext args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + args.createSnapshot() + " for " + console.getName() + ", but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        console.sendMessages(Text.of(getSubCommandsHelpMessage()));
    }

    /**
     * Executes the command
     * You can Override this method
     *
     * @param player The commander who is executing this command
     * @param args   The parsed command arguments for this command
     */
    public void exec(@NotNull Player player, @NotNull CommandContext args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + args.createSnapshot() + " for " + player.getName() + ", but the command is not implemented. Exec type: User, CommandContext");
        }

        player.sendMessages(Text.of(getSubCommandsHelpMessage()));
    }

}
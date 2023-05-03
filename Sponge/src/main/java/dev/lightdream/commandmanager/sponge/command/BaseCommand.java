package dev.lightdream.commandmanager.sponge.command;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.annotation.Command;
import dev.lightdream.commandmanager.common.command.CommonCommandImpl;
import dev.lightdream.commandmanager.sponge.CommandMain;
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

@SuppressWarnings("unused")
public abstract class BaseCommand extends CommonCommandImpl implements CommandExecutor {

    public CommandSpecWrap spec;

    @Override
    public final boolean registerCommand() {
        this.spec = CommandSpecWrap.builder().build();

        Command command = getCommandAnnotation();

        String permission = getPermission();
        if (!permission.equals("")) {
            spec.spec.permission(permission);
        }
        spec.onlyForConsole = command.onlyForConsole();
        spec.onlyForPlayers = command.onlyForPlayers();

        Sponge.getCommandManager().register(CommonCommandMain.getCommandMain(CommandMain.class), getCommandSpec(), command.aliases());
        return true;
    }

    public final CommandSpec getCommandSpec() {
        getSubCommands().forEach(commandObject -> {
            BaseCommand command = (BaseCommand) commandObject;
            spec.spec.child(command.getCommandSpec(), getAliasList());
        });
        spec.spec.executor(this);
        spec.spec.arguments(getArgs().toArray(new CommandElement[0]));
        return spec.spec.build();
    }

    /**
     * Gets the command string
     *
     * @return The command string
     */
    public List<CommandElement> getArgs() {
        return new ArrayList<>();
    }

    @Override
    public final @NotNull CommandResult execute(@NotNull CommandSource src, @NotNull CommandContext args) {
        LambdaExecutor executor = () -> {
            if (spec.onlyForConsole) {
                if (!(src instanceof ConsoleSource)) {
                    src.sendMessage(Text.of(CommonCommandMain.getCommandMain(CommandMain.class).getLang().onlyForConsole));
                    return;
                }
                exec((ConsoleSource) src, args);
                return;
            }
            if (spec.onlyForPlayers) {
                if (!(src instanceof Player)) {
                    src.sendMessage(Text.of(CommonCommandMain.getCommandMain(CommandMain.class).getLang().onlyFotPlayer));
                    return;
                }
                Player player = (Player) src;
                exec(player, args);
                return;
            }

            exec(src, args);
        };

        if (async()) {
            ScheduleUtils.runTaskAsync(executor);
        } else {
            executor.execute();
        }

        return CommandResult.success();
    }

    /**
     * Executes the command
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

    @Override
    public final boolean checkPermission(Object user, String permission) {
        return ((Player) user).hasPermission(permission);
    }

    @Override
    public final void sendMessage(Object user, String message) {
        CommandSource source = (CommandSource) user;
        source.sendMessage(Text.of(message));
    }
}
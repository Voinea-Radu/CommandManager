package dev.lightdream.commandmanager.fabric.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.fabric.CommandMain;
import dev.lightdream.commandmanager.fabric.utils.PermissionUtils;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

@SuppressWarnings("unused")
public abstract class BaseCommand implements CommonCommand {

    public static String commandSourceFiled = "field_9819";

    public List<CommonCommand> subCommands = new ArrayList<>();

    public BaseCommand() {
        Debugger.log("Constructing " + this.getClass().getName());
        this.init();
    }

    public List<RequiredArgumentBuilder<ServerCommandSource, ?>> getArguments() {
        return new ArrayList<>();
    }

    protected LiteralArgumentBuilder<ServerCommandSource> getCommandBuilder() {
        LiteralArgumentBuilder<ServerCommandSource> command = literal(getCommand());

        List<CommonCommand> subCommands = getSubCommands();
        List<RequiredArgumentBuilder<ServerCommandSource, ?>> arguments = getArguments();

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

        RequiredArgumentBuilder<ServerCommandSource, ?> then = null;

        Debugger.log(getClass().getName() + " arguments size: " + arguments.size());

        if (arguments.size() != 0) {
            if (arguments.size() != 1) {
                for (int index = arguments.size() - 2; index >= 0; index--) {
                    Debugger.log(getClass().getName() + " Adding argument " + (index + 1) + " to " + index + " command");
                    arguments.get(index).then(arguments.get(index + 1));
                }
            }
            then = arguments.get(0);
        }

        if (then != null) {
            Debugger.log(getClass().getName() + " Adding arguments");
            command.then(then);
        }

        command.executes(context -> {
            try {
                Debugger.log(getClass().getName() + "Executing...");
                return execute(context);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return 0;
        });

        return command;
    }

    @Override
    public final boolean registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(getCommandBuilder()));
        return true;
    }

    @SneakyThrows
    private CommandOutput getSource(CommandContext<ServerCommandSource> context) {
        Field field = ServerCommandSource.class.getDeclaredField(commandSourceFiled);
        field.setAccessible(true);
        return (CommandOutput) field.get(context.getSource());
    }

    public final int execute(CommandContext<ServerCommandSource> context) {
        CommandOutput source = getSource(context);

        if (onlyForConsole()) {
            if (!(source instanceof MinecraftServer consoleSource)) {
                sendMessage(source, CommonCommandMain.getCommandMain(CommandMain.class).getLang().onlyForConsole);
                return 0;
            }
            exec(consoleSource, context);
            return 0;
        }
        if (onlyForPlayers()) {
            if (!(source instanceof ServerPlayerEntity player)) {
                sendMessage(source, CommonCommandMain.getCommandMain(CommandMain.class).getLang().onlyFotPlayer);
                return 0;
            }
            exec(player, context);
            return 0;
        }

        exec(source, context);
        return 0;
    }

    public void exec(@NotNull CommandOutput console, @NotNull CommandContext<ServerCommandSource> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommand() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        sendMessage(console, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    public void exec(@NotNull MinecraftServer console, @NotNull CommandContext<ServerCommandSource> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommand() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        sendMessage(console, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    public void exec(@NotNull ServerPlayerEntity player, @NotNull CommandContext<ServerCommandSource> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommand() + " for " + player.getName() + ", but the command is not implemented. Exec type: User, CommandContext");
        }

        sendMessage(player, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    @Override
    public final boolean checkPermission(Object user, String permission) {
        return PermissionUtils.checkPermission((ServerPlayerEntity) user, permission);
    }

    @Override
    public final void sendMessage(Object user, String message) {
        if (user instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.of(message));
            return;
        }
        if (user instanceof ServerCommandSource source) {
            source.sendFeedback(net.minecraft.text.Text.of(message), false);
            return;
        }

        throw new RuntimeException("Can only send messages to objects of type ServerPlayerEntity and ServerCommandSource." +
                " Trying to send message to " + user.getClass());
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

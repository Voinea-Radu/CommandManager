package dev.lightdream.commandmanager.fabric.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.command.CommonCommandImpl;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.fabric.CommandMain;
import dev.lightdream.commandmanager.fabric.utils.PermissionUtils;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

@SuppressWarnings("unused")
public abstract class BaseCommand extends CommonCommandImpl {

    public static String commandSourceFiled = "field_9819";

    public List<ArgumentBuilder<ServerCommandSource, ?>> getArguments() {
        return new ArrayList<>();
    }

    protected LiteralArgumentBuilder<ServerCommandSource> getCommandBuilder() {
        LiteralArgumentBuilder<ServerCommandSource> command = literal(getCommandString());

        List<ICommonCommand> subCommands = getSubCommands();
        List<ArgumentBuilder<ServerCommandSource, ?>> arguments = getArguments();

        if (subCommands == null) {
            subCommands = new ArrayList<>();
        }
        if (arguments == null) {
            arguments = new ArrayList<>();
        }

        for (ICommonCommand subCommandObject : subCommands) {
            BaseCommand subCommand = (BaseCommand) subCommandObject;
            command.then(subCommand.getCommandBuilder());
        }

        ArgumentBuilder<ServerCommandSource, ?> then = null;

        if (arguments.size() != 0) {
            arguments.get(arguments.size() - 1).executes(this::executeCatch);
            if (arguments.size() != 1) {
                for (int index = arguments.size() - 2; index >= 0; index--) {
                    arguments.get(index).then(arguments.get(index + 1));
                }
            }
            then = arguments.get(0);
        } else {
            command.executes(this::executeCatch);
        }

        if (then != null) {
            command.then(then);
        }


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

    public final int executeCatch(CommandContext<ServerCommandSource> context) {
        try {
            return execute(context);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return 0;
    }

    public final int execute(CommandContext<ServerCommandSource> context) {
        CommandOutput source = getSource(context);

        if (!isEnabled()) {
            sendMessage(source, CommonCommandMain.getCommandMain(CommandMain.class).getLang().commandIsDisabled);
            return 0;
        }

        if (!checkPermission(source, getPermission())) {
            sendMessage(source, CommonCommandMain.getCommandMain(CommandMain.class).getLang().noPermission);
            return 0;
        }

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

    public void exec(@NotNull CommandOutput source, @NotNull CommandContext<ServerCommandSource> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommandString() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        sendMessage(source, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    public void exec(@NotNull MinecraftServer console, @NotNull CommandContext<ServerCommandSource> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommandString() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        sendMessage(console, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    public void exec(@NotNull ServerPlayerEntity player, @NotNull CommandContext<ServerCommandSource> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getCommandString() + " for " + player.getName() + ", but the command is not implemented. Exec type: User, CommandContext");
        }

        sendMessage(player, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    @Override
    public final boolean checkPermission(Object sender, String permission) {
        if (sender instanceof ServerPlayerEntity player) {
            return PermissionUtils.checkPermission((ServerPlayerEntity) sender, permission);
        }

        return sender instanceof MinecraftDedicatedServer source;
    }

    @Override
    public final void sendMessage(Object user, String message) {
        if (user instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.of(message));
            return;
        }
        if (user instanceof MinecraftDedicatedServer source) {
            source.sendMessage(net.minecraft.text.Text.of(message));
            return;
        }

        throw new RuntimeException("Can only send messages to objects of type ServerPlayerEntity and MinecraftDedicatedServer." +
                " Trying to send message to " + user.getClass());
    }
}

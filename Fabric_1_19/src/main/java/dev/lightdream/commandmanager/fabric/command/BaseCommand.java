package dev.lightdream.commandmanager.fabric.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.fabric.CommandMain;
import dev.lightdream.commandmanager.fabric.utils.*;
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

import static net.minecraft.server.command.CommandManager.*;

@SuppressWarnings("unused")
public abstract class BaseCommand implements CommonCommand {

    public static String commandSourceFiled = "field_9819";

    public List<CommonCommand> subCommands = new ArrayList<>();

    public BaseCommand() {
        this.init();
    }

    public List<RequiredArgumentBuilder<ServerCommandSource, ?>> getArguments(){
        return new ArrayList<>();
    }

    public LiteralArgumentBuilder<ServerCommandSource> getCommandBuilder(){
        LiteralArgumentBuilder<ServerCommandSource> builder = literal(getCommand())
                .executes(this::execute);

        for (RequiredArgumentBuilder<ServerCommandSource, ?> argument : getArguments()) {
            builder.then(argument);
        }

        for (CommonCommand subCommand : getSubCommands()) {
            BaseCommand command = (BaseCommand) subCommand;

            builder.then(command.getCommandBuilder());
        }

        return builder;
    }

    @Override
    public final boolean registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(getCommandBuilder()));
        return true;
    }

    @SneakyThrows
    private CommandOutput getSource(CommandContext<ServerCommandSource> context){
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

        sendMessage(player,ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    @Override
    public final boolean checkPermission(Object user, String permission) {
        return PermissionUtils.checkPermission((ServerPlayerEntity) user, permission);
    }

    @Override
    public final void sendMessage(Object user, String message) {
        if(user instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.of(message));
            return;
        }
        if (user instanceof ServerCommandSource source){
            source.sendFeedback(net.minecraft.text.Text.of(message), false);
            return;
        }

        throw new RuntimeException("Can only send messages to objects of type ServerPlayerEntity and ServerCommandSource");
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

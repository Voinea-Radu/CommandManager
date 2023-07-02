package dev.lightdream.commandmanager.forge.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.command.CommonCommandImpl;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.forge.CommandMain;
import dev.lightdream.commandmanager.forge.util.PermissionUtil;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class BaseCommand extends CommonCommandImpl {

    public static String[] commandSourceFiled = {"field_9819", "f_81288_"};

    @Override
    public final boolean registerCommand(String name) {
        CommonCommandMain.getCommandMain(CommandMain.class).getDispatcher().register(getCommandBuilder(name));
        return true;
    }

    public List<RequiredArgumentBuilder<CommandSourceStack, ?>> getArguments() {
        return new ArrayList<>();
    }

    private LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(String name) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal(name);

        List<ICommonCommand> subCommands = getSubCommands();
        List<RequiredArgumentBuilder<CommandSourceStack, ?>> arguments = getArguments();

        if (subCommands == null) {
            subCommands = new ArrayList<>();
        }
        if (arguments == null) {
            arguments = new ArrayList<>();
        }

        for (ICommonCommand subCommandObject : subCommands) {
            BaseCommand subCommand = (BaseCommand) subCommandObject;
            for (String subName : subCommand.getNames()) {
                command.then(subCommand.getCommandBuilder(subName));
            }
        }

        RequiredArgumentBuilder<CommandSourceStack, ?> then = null;

        if (arguments.size() != 0) {
            if (arguments.size() != 1) {
                for (int index = arguments.size() - 2; index >= 0; index--) {
                    arguments.get(index).then(arguments.get(index + 1));
                }
            }
            then = arguments.get(0);
        }

        if (then != null) {
            command.then(then);
        }

        command.executes(context -> {
            try {
                return internalExecute(context);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return 0;
        });

        return command;
    }

    @SneakyThrows
    private CommandSource getCommandSource(CommandContext<CommandSourceStack> context) {
        return getCommandSource(context, 0);
    }

    @SneakyThrows
    private CommandSource getCommandSource(CommandContext<CommandSourceStack> context, int index) {
        if (index == commandSourceFiled.length) {
            return null;
        }

        try {
            Field field = CommandSourceStack.class.getDeclaredField(commandSourceFiled[index]);
            return (CommandSource) field.get(context.getSource());
        } catch (Exception e) {
            return getCommandSource(context, index + 1);
        }
    }

    public int internalExecute(CommandContext<CommandSourceStack> context) {
        CommandSource source = getCommandSource(context);

        if(!isEnabled()){
            sendMessage(source, CommonCommandMain.getCommandMain(CommandMain.class).getLang().commandIsDisabled);
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
            if (!(source instanceof Player player)) {
                sendMessage(source, CommonCommandMain.getCommandMain(CommandMain.class).getLang().onlyFotPlayer);
                return 0;
            }
            exec(player, context);
            return 0;
        }

        exec(source, context);
        return 0;
    }

    public void exec(@NotNull CommandSource source, @NotNull CommandContext<CommandSourceStack> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getName() + " for " + source + ", but the command is not implemented. Exec type: CommandSource, CommandContext");
        }

        sendMessage(source, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    public void exec(@NotNull MinecraftServer console, @NotNull CommandContext<CommandSourceStack> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getName() + " for Console, but the command is not implemented. Exec type: ConsoleSource, CommandContext");
        }

        sendMessage(console, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    public void exec(@NotNull Player player, @NotNull CommandContext<CommandSourceStack> context) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + getName() + " for " + player.getName() + ", but the command is not implemented. Exec type: User, CommandContext");
        }

        sendMessage(player, ListUtils.listToString(getSubCommandsHelpMessage(), "\n"));
    }

    @Override
    public final boolean checkPermission(Object user, String permission) {
        return PermissionUtil.checkPermission((Player) user, permission);
    }

    @Override
    public final void sendMessage(Object user, String message) {
        if (user instanceof ServerPlayer player) {
            player.displayClientMessage(Component.literal(message), false);
            return;
        }
        if (user instanceof MinecraftServer) {
            Logger.info(message);
        }
    }
}
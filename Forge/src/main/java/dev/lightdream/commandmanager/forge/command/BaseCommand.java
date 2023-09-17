package dev.lightdream.commandmanager.forge.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.common.command.CommonCommandImpl;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.platform.PermissionUtils;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.forge.CommandMain;
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
        ((CommandMain) getMain()).getDispatcher().register(getCommandBuilder(name));
        return true;
    }

    private @NotNull List<RequiredArgumentBuilder<CommandSourceStack, String>> getArgumentsBuilders() {
        List<RequiredArgumentBuilder<CommandSourceStack, String>> output = new ArrayList<>();

        for (String argument : getArguments()) {
            RequiredArgumentBuilder<CommandSourceStack, String> argumentBuilder =
                    RequiredArgumentBuilder.argument(argument, StringArgumentType.string());

            argumentBuilder.suggests((context, builder) -> {
                for (String suggestion : suggest(argument)) {
                    builder.suggest(suggestion);
                }
                return builder.buildFuture();
            });

            output.add(argumentBuilder);
        }

        return output;
    }

    private LiteralArgumentBuilder<CommandSourceStack> getCommandBuilder(String name) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal(name);

        List<ICommonCommand> subCommands = getSubCommands();
        List<RequiredArgumentBuilder<CommandSourceStack, String>> arguments = getArgumentsBuilders();

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

        if (!arguments.isEmpty()) {
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
                //noinspection CallToPrintStackTrace
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
        List<String> argumentsList = convertToArgumentsList(context);

        if (!isEnabled()) {
            sendMessage(source, getMain().getLang().commandIsDisabled);
            return 0;
        }

        if (onlyForConsole()) {
            if (!(source instanceof MinecraftServer consoleSource)) {
                sendMessage(source, getMain().getLang().onlyForConsole);
                return 0;
            }
            exec(new PlatformConsole(consoleSource), argumentsList);
            return 0;
        }
        if (onlyForPlayers()) {
            if (!(source instanceof Player player)) {
                sendMessage(source, getMain().getLang().onlyFotPlayer);
                return 0;
            }
            exec(new PlatformPlayer(player), argumentsList);
            return 0;
        }

        exec(new PlatformCommandSender(source), argumentsList);
        return 0;
    }

    public List<String> convertToArgumentsList(CommandContext<CommandSourceStack> context) {
        List<String> arguments = new ArrayList<>();

        for (String argument : getArguments()) {
            arguments.add(context.getArgument(argument, String.class));
        }

        return arguments;
    }



    @Override
    public final boolean checkPermission(Object user, String permission) {
        return PermissionUtils.checkPermission(new PlatformPlayer<>(user), permission);
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
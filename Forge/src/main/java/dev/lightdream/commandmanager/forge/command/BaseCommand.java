package dev.lightdream.commandmanager.forge.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.common.command.CommonCommandImpl;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.dto.ArgumentList;
import dev.lightdream.commandmanager.common.platform.PermissionUtils;
import dev.lightdream.commandmanager.forge.CommandMain;
import dev.lightdream.commandmanager.forge.platform.ForgeAdapter;
import dev.lightdream.logger.Logger;
import lombok.SneakyThrows;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class BaseCommand extends CommonCommandImpl {

    public static String[] commandSourceFiled = {"field_9819", "f_81288_"};

    @Override
    public final boolean registerCommand(String name) {
        getMain().getDispatcher().register(getCommandBuilder(name));
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
        CommandSource sender = getCommandSource(context);
        List<String> argumentsList = convertToArgumentsList(context);
        ArgumentList arguments = new ArgumentList(argumentsList, this);

        if (!isEnabled()) {
            sendMessage(sender, getMain().getLang().commandIsDisabled);
            return 0;
        }

        if (onlyForConsole()) {
            if (!(sender instanceof MinecraftServer consoleSource)) {
                sendMessage(sender, getMain().getLang().onlyForConsole);
                return 0;
            }
            exec(getAdapter().convertConsole(consoleSource), arguments);
            return 0;
        }
        if (onlyForPlayers()) {
            if (!(sender instanceof ServerPlayer player)) {
                sendMessage(sender, getMain().getLang().onlyFotPlayer);
                return 0;
            }
            exec(getAdapter().convertPlayer(player), arguments);
            return 0;
        }

        exec(getAdapter().convertCommandSender(sender), arguments);
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
    public CommandMain getMain() {
        return (CommandMain) super.getMain();
    }

    protected ForgeAdapter getAdapter() {
        return (ForgeAdapter) getMain().getAdapter();
    }

    @Override
    public final boolean checkPermission(Object user, String permission) {
        if (user instanceof MinecraftServer console) {
            return true;
        }

        if (user instanceof ServerPlayer player) {
            return PermissionUtils.checkPermission(getAdapter().convertPlayer((ServerPlayer) user), permission);
        }

        return false;
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
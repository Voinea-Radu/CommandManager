package dev.lightdream.commandmanager.forge.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.command.ICommand;
import dev.lightdream.commandmanager.common.command.IPlatformCommand;
import dev.lightdream.commandmanager.common.dto.ArgumentList;
import dev.lightdream.commandmanager.common.platform.PermissionUtils;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.forge.CommandMain;
import dev.lightdream.commandmanager.forge.platform.ForgeAdapter;
import dev.lightdream.commandmanager.forge.platform.ForgeConsole;
import dev.lightdream.commandmanager.forge.platform.ForgePlayer;
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
public class ForgeCommand implements IPlatformCommand {


    private final static String[] commandSourceFiled = {"field_9819", "f_81288_"};

    private CommonCommand commonCommand;

    public ForgeCommand(CommonCommand commonCommand) {
        this.commonCommand = commonCommand;
    }

    @Override
    public CommonCommand getCommonCommand() {
        return commonCommand;
    }

    @Override
    public void setCommonCommand(CommonCommand commonCommand) {
        this.commonCommand = commonCommand;
    }


    private @NotNull List<RequiredArgumentBuilder<CommandSourceStack, String>> createArgumentsBuilders() {
        List<RequiredArgumentBuilder<CommandSourceStack, String>> output = new ArrayList<>();

        for (String argument : getArguments()) {
            RequiredArgumentBuilder<CommandSourceStack, String> argumentBuilder =
                    RequiredArgumentBuilder.argument(argument, StringArgumentType.string());

            argumentBuilder.suggests((context, builder) -> {
                CommandSource sender = getCommandSource(context);

                if (!(sender instanceof ServerPlayer player)) {
                    return builder.buildFuture();
                }

                String lastArgument = builder.getRemaining();

                List<String> suggestions = suggest(getAdapter().convertPlayer(player), argument);
                suggestions = ListUtils.getListThatStartsWith(suggestions, lastArgument);

                for (String suggestion : suggestions) {
                    builder.suggest(suggestion);
                }
                return builder.buildFuture();
            });

            output.add(argumentBuilder);
        }

        return output;
    }

    private LiteralArgumentBuilder<CommandSourceStack> createCommandBuilder(String name) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal(name);

        List<ICommand> subCommands = getSubCommands();
        List<RequiredArgumentBuilder<CommandSourceStack, String>> arguments = createArgumentsBuilders();

        if (subCommands == null) {
            subCommands = new ArrayList<>();
        }

        for (ICommand subCommandObject : subCommands) {
            ForgeCommand subCommand = (ForgeCommand) subCommandObject;

            for (String subName : subCommand.getNames()) {
                command.then(subCommand.createCommandBuilder(subName));
            }
        }

        RequiredArgumentBuilder<CommandSourceStack, ?> then = null;

        if (!arguments.isEmpty()) {
            arguments.get(arguments.size() - 1).executes(this::executeCatch);
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

        command.executes(this::executeCatch);

        return command;
    }


    private int executeCatch(CommandContext<CommandSourceStack> context) {
        try {
            internalExecute(context);
        } catch (Throwable t) {
            //noinspection CallToPrintStackTrace
            t.printStackTrace();
        }
        return 0;
    }

    public void internalExecute(CommandContext<CommandSourceStack> context) {
        PlatformCommandSender sender = getAdapter().convertCommandSender(getCommandSource(context));
        List<String> argumentsList = convertToArgumentsList(context);
        ArgumentList arguments = new ArgumentList(argumentsList, getCommonCommand());

        if (!isEnabled()) {
            sender.sendMessage(getMain().getLang().commandIsDisabled);
            return;
        }

        switch (getOnlyFor()) {
            case PLAYER -> {
                if (!(sender instanceof ForgePlayer player)) {
                    sender. sendMessage(getMain().getLang().onlyFotPlayer);
                    return;
                }
                execute(player, arguments);
            }
            case CONSOLE -> {
                if (!(sender instanceof ForgeConsole console)) {
                    sender.sendMessage( getMain().getLang().onlyForConsole);
                    return;
                }
                execute(console, arguments);
            }
            case BOTH -> execute(sender, arguments);
        }
    }

    private List<String> convertToArgumentsList(CommandContext<CommandSourceStack> context) {
        List<String> arguments = new ArrayList<>();

        for (String argument : getArguments()) {
            arguments.add(context.getArgument(argument, String.class));
        }

        return arguments;
    }

    @Override
    public final boolean registerCommand(String name) {
        getMain().getDispatcher().register(createCommandBuilder(name));
        return true;
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

    @Override
    public CommandMain getMain() {
        return (CommandMain) IPlatformCommand.super.getMain();
    }

    protected ForgeAdapter getAdapter() {
        return getMain().getAdapter();
    }
}
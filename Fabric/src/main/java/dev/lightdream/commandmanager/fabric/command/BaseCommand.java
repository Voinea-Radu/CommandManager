package dev.lightdream.commandmanager.fabric.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.lightdream.commandmanager.common.command.CommonCommandImpl;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.dto.ArgumentList;
import dev.lightdream.commandmanager.common.platform.PermissionUtils;
import dev.lightdream.commandmanager.common.utils.ListUtils;
import dev.lightdream.commandmanager.fabric.CommandMain;
import dev.lightdream.commandmanager.fabric.platform.FabricAdapter;
import lombok.SneakyThrows;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
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
public abstract class BaseCommand extends CommonCommandImpl {

    @SuppressWarnings("FieldCanBeLocal")
    private static final String commandSourceFiled = "field_9819";

    @SneakyThrows
    private static CommandOutput getSource(CommandContext<ServerCommandSource> context) {
        //noinspection JavaReflectionMemberAccess
        Field field = ServerCommandSource.class.getDeclaredField(commandSourceFiled);
        field.setAccessible(true);
        return (CommandOutput) field.get(context.getSource());
    }

    @Override
    public boolean registerCommand(String alias) {
        getMain().getServer().getCommandManager().getDispatcher().register(createCommandBuilder(alias));
        getMain().getServer().getPlayerManager().getPlayerList().forEach(player ->
                getMain().getServer().getCommandManager().sendCommandTree(player)
        );

        return true;
    }

    @Override
    public final void sendMessage(Object user, String message) {
        if (user instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.of(message));
            return;
        }

        if (user instanceof MinecraftServer source) {
            source.sendMessage(net.minecraft.text.Text.of(message));
            return;
        }

        throw new RuntimeException("Can only send messages to objects of type ServerPlayerEntity and MinecraftDedicatedServer." +
                " Trying to send message to " + user.getClass());
    }

    @Override
    public final boolean checkPermission(Object sender, String permission) {
        if (sender instanceof ServerPlayerEntity player) {
            return PermissionUtils.checkPermission(getAdapter().convertPlayer(player), permission);
        }

        return sender instanceof MinecraftServer;
    }

    @Override
    public CommandMain getMain() {
        return (CommandMain) super.getMain();
    }

    protected FabricAdapter getAdapter() {
        return (FabricAdapter) getMain().getAdapter();
    }

    private @NotNull List<RequiredArgumentBuilder<ServerCommandSource, String>> createArgumentsBuilders() {
        List<RequiredArgumentBuilder<ServerCommandSource, String>> output = new ArrayList<>();

        for (String argument : getArguments()) {
            output.add(
                    CommandManager.argument(argument, StringArgumentType.string())
                            .suggests((context, builder) -> {
                                CommandOutput sender = getSource(context);

                                if (!(sender instanceof ServerPlayerEntity player)) {
                                    return builder.buildFuture();
                                }

                                String lastArgument = builder.getRemaining();

                                List<String> suggestions = suggest(getAdapter().convertPlayer(player), argument);
                                suggestions = ListUtils.getListThatStartsWith(suggestions, lastArgument);

                                for (String suggestion : suggestions) {
                                    builder.suggest(suggestion);
                                }

                                return builder.buildFuture();
                            })
            );
        }

        return output;
    }

    private LiteralArgumentBuilder<ServerCommandSource> createCommandBuilder(String name) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal(name);

        List<ICommonCommand> subCommands = getSubCommands();
        List<RequiredArgumentBuilder<ServerCommandSource, String>> arguments = createArgumentsBuilders();

        for (ICommonCommand subCommandObject : subCommands) {
            BaseCommand subCommand = (BaseCommand) subCommandObject;

            for (String subName : subCommand.getArguments()) {
                command.then(subCommand.createCommandBuilder(subName));
            }
        }

        ArgumentBuilder<ServerCommandSource, ?> then = null;

        if (!arguments.isEmpty()) {
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

    private int executeCatch(CommandContext<ServerCommandSource> context) {
        try {
            internalExecute(context);
        } catch (Throwable t) {
            //noinspection CallToPrintStackTrace
            t.printStackTrace();
        }
        return 0;
    }

    private void internalExecute(CommandContext<ServerCommandSource> context) {
        CommandOutput sender = getSource(context);
        List<String> argumentsList = convertToArgumentsList(context);
        ArgumentList arguments = new ArgumentList(argumentsList, this);

        if (!isEnabled()) {
            sendMessage(sender, getMain().getLang().commandIsDisabled);
            return;
        }

        if (!checkPermission(sender, getPermission())) {
            sendMessage(sender, getMain().getLang().noPermission);
            return;
        }

        switch (getOnlyFor()) {
            case PLAYER -> {
                if (!(sender instanceof ServerPlayerEntity player)) {
                    sendMessage(sender, getMain().getLang().onlyFotPlayer);
                    return;
                }
                execute(getAdapter().convertPlayer(player), arguments);
            }
            case CONSOLE -> {
                if (!(sender instanceof MinecraftServer consoleSource)) {
                    sendMessage(sender, getMain().getLang().onlyForConsole);
                    return;
                }
                execute(getAdapter().convertConsole(consoleSource), arguments);
            }
            case BOTH -> execute(getAdapter().convertCommandSender(sender), arguments);
        }
    }

    public List<String> convertToArgumentsList(CommandContext<ServerCommandSource> context) {
        List<String> arguments = new ArrayList<>();

        for (String argument : getArguments()) {
            arguments.add(context.getArgument(argument, String.class));
        }

        return arguments;
    }
}

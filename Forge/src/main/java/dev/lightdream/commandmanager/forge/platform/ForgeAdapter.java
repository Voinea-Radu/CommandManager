package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.forge.command.BaseCommand;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ForgeAdapter extends Adapter {
    @Override
    public ServerPlayer convertPlayer(PlatformPlayer player) {
        return (ServerPlayer) player.getNativePlayer();
    }

    @Override
    public <T> PlatformPlayer convertPlayer(T playerObject) {
        if (!(playerObject instanceof ServerPlayer player)) {
            throw new RuntimeException("Can not convert from " + playerObject.getClass().getName() + " to " +
                    PlatformPlayer.class.getName());
        }
        return new ForgePlayer(player);
    }

    @Override
    public CommandSource convertCommandSender(PlatformCommandSender commandSender) {
        return (CommandSource) commandSender.getNativeCommandSender();
    }

    @Override
    public <T> PlatformCommandSender convertCommandSender(T commandSenderObject) {
        if (!(commandSenderObject instanceof CommandSource commandSender)) {
            throw new RuntimeException("Can not convert from " + commandSenderObject.getClass().getName() + " to " +
                    PlatformCommandSender.class.getName());
        }
        return new ForgeCommandSender(commandSender);
    }

    @Override
    public MinecraftServer convertConsole(PlatformConsole console) {
        return (MinecraftServer) console.getNativeConsole();
    }

    @Override
    public <T> PlatformConsole convertConsole(T consoleObject) {
        if (!(consoleObject instanceof MinecraftServer console)) {
            throw new RuntimeException("Can not convert from " + consoleObject.getClass().getName() + " to " +
                    PlatformConsole.class.getName());
        }
        return new ForgeConsole(console);
    }
}

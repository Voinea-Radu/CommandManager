package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricAdapter extends Adapter {

    @Override
    public ServerPlayerEntity convertPlayer(PlatformPlayer player) {
        return (ServerPlayerEntity) player.getNativePlayer();
    }

    @Override
    public <T> PlatformPlayer convertPlayer(T playerObject) {
        if (!(playerObject instanceof ServerPlayerEntity player)) {
            throw new RuntimeException("Can not convert from " + playerObject.getClass().getName() + " to " +
                    PlatformPlayer.class.getName());
        }
        return new FabricPlayer(player);
    }

    @Override
    public CommandOutput convertCommandSender(PlatformCommandSender commandSender) {
        return (CommandOutput) commandSender.getNativeCommandSender();
    }

    @Override
    public <T> PlatformCommandSender convertCommandSender(T commandSenderObject) {
        if (!(commandSenderObject instanceof ServerPlayerEntity commandSender)) {
            throw new RuntimeException("Can not convert from " + commandSenderObject.getClass().getName() + " to " +
                    PlatformCommandSender.class.getName());
        }
        return new FabricCommandSender(commandSender);
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
        return new FabricConsole(console);
    }
}


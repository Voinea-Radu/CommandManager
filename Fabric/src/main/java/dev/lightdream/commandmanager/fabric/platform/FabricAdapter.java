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
    public <T> FabricPlayer convertPlayer(T playerObject) {
        if (!(playerObject instanceof ServerPlayerEntity player)) {
            throw createConversionError(playerObject, FabricPlayer.class);
        }

        return new FabricPlayer(player);
    }

    @Override
    public CommandOutput convertCommandSender(PlatformCommandSender commandSender) {
        return (CommandOutput) commandSender.getNativeCommandSender();
    }

    @Override
    public <T> FabricCommandSender convertCommandSender(T commandSenderObject) {
        if (!(commandSenderObject instanceof ServerPlayerEntity commandSender)) {
            throw createConversionError(commandSenderObject, PlatformCommandSender.class);
        }

        return new FabricCommandSender(commandSender);
    }

    @Override
    public MinecraftServer convertConsole(PlatformConsole console) {
        return (MinecraftServer) console.getNativeConsole();
    }

    @Override
    public <T> FabricConsole convertConsole(T consoleObject) {
        if (!(consoleObject instanceof MinecraftServer console)) {
            throw createConversionError(consoleObject, FabricConsole.class);
        }

        return new FabricConsole(console);
    }
}


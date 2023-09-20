package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ForgeAdapter extends Adapter {
    @Override
    public ServerPlayer convertPlayer(PlatformPlayer player) {
        return (ServerPlayer) player.getNativePlayer();
    }

    @Override
    public <T> ForgePlayer convertPlayer(T playerObject) {
        if (!(playerObject instanceof ServerPlayer player)) {
            throw createConversionError(playerObject, ForgePlayer.class);
        }

        return new ForgePlayer(player);
    }

    @Override
    public CommandSource convertCommandSender(PlatformCommandSender commandSender) {
        return (CommandSource) commandSender.getNativeCommandSender();
    }

    @Override
    public <T> ForgeCommandSender convertCommandSender(T commandSenderObject) {
        if (!(commandSenderObject instanceof CommandSource commandSender)) {
            throw createConversionError(commandSenderObject, ForgeCommandSender.class);
        }

        return new ForgeCommandSender(commandSender);
    }

    @Override
    public MinecraftServer convertConsole(PlatformConsole console) {
        return (MinecraftServer) console.getNativeConsole();
    }

    @Override
    public <T> ForgeConsole convertConsole(T consoleObject) {
        if (!(consoleObject instanceof MinecraftServer console)) {
            throw createConversionError(consoleObject, PlatformConsole.class);
        }

        return new ForgeConsole(console);
    }
}

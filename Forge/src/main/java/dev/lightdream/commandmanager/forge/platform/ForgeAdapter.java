package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.command.IPlatformCommand;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.forge.command.ForgeCommand;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface ForgeAdapter extends Adapter<ServerPlayer, MinecraftServer, CommandSource> {

    @Override
    default ForgePlayer convertPlayer(ServerPlayer player) {
        return new ForgePlayer(player, this);
    }

    @Override
    default ForgeConsole convertConsole(MinecraftServer console) {
        return new ForgeConsole(console, this);
    }

    @Override
    default IPlatformCommand convertCommand(CommonCommand command) {
        return new ForgeCommand(command);
    }

    @Override
    default Class<ServerPlayer> getNativePlayerClass() {
        return ServerPlayer.class;
    }

    @Override
    default Class<MinecraftServer> getNativeConsoleClass() {
        return MinecraftServer.class;
    }

}

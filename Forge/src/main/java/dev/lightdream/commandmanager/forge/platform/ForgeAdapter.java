package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.command.IPlatformCommand;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.forge.command.ForgeCommand;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ForgeAdapter extends Adapter<ServerPlayer, MinecraftServer,CommandSource> {

    @Override
    public  ForgePlayer convertPlayer(ServerPlayer player) {
        return new ForgePlayer(player);
    }

    @Override
    public ForgeConsole convertConsole(MinecraftServer console) {
        return new ForgeConsole(console);
    }

    @Override
    public IPlatformCommand convertCommand(CommonCommand command) {
        return new ForgeCommand(command);
    }

    @Override
    protected Class<ServerPlayer> getNativePlayerClass() {
        return ServerPlayer.class;
    }

    @Override
    protected Class<MinecraftServer> getNativeConsoleClass() {
        return MinecraftServer.class;
    }

}

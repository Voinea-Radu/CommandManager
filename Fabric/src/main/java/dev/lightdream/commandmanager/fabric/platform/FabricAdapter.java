package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.fabric.command.FabricCommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricAdapter extends Adapter<ServerPlayerEntity, MinecraftServer, CommandOutput> {

    @Override
    public FabricPlayer convertPlayer(ServerPlayerEntity nativePlayer) {
        return new FabricPlayer(nativePlayer, this);
    }

    @Override
    public FabricConsole convertConsole(MinecraftServer nativeConsole) {
        return new FabricConsole(nativeConsole, this);
    }

    @Override
    public FabricCommand convertCommand(CommonCommand command) {
        return new FabricCommand(command);
    }

    @Override
    protected Class<ServerPlayerEntity> getNativePlayerClass() {
        return ServerPlayerEntity.class;
    }

    @Override
    protected Class<MinecraftServer> getNativeConsoleClass() {
        return MinecraftServer.class;
    }


}


package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.command.CommonCommand;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.fabric.command.FabricCommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;

public interface FabricAdapter extends Adapter<ServerPlayerEntity, MinecraftServer, CommandOutput> {

    @Override
    default FabricPlayer convertPlayer(ServerPlayerEntity nativePlayer) {
        return new FabricPlayer(nativePlayer, this);
    }

    @Override
    default FabricConsole convertConsole(MinecraftServer nativeConsole) {
        return new FabricConsole(nativeConsole, this);
    }

    @Override
    default FabricCommand convertCommand(CommonCommand command) {
        return new FabricCommand(command);
    }

    @Override
    default Class<ServerPlayerEntity> getNativePlayerClass() {
        return ServerPlayerEntity.class;
    }

    @Override
    default Class<MinecraftServer> getNativeConsoleClass() {
        return MinecraftServer.class;
    }


}


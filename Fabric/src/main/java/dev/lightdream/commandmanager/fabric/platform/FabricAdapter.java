package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.fabric.command.BaseCommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricAdapter extends Adapter<ServerPlayerEntity,CommandOutput, MinecraftServer , BaseCommand> {
    @Override
    public PlatformPlayer<ServerPlayerEntity> convertPlayer(ServerPlayerEntity player) {
        return new FabricPlayer(player);
    }

    @Override
    public PlatformCommandSender<CommandOutput> convertCommandSender(CommandOutput commandOutput) {
        return new FabricCommandSender(commandOutput);
    }

    @Override
    public PlatformConsole<MinecraftServer> convertConsole(MinecraftServer server) {
        return new FabricConsole(server);
    }
}

package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.commandmanager.forge.command.BaseCommand;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ForgeAdapter extends Adapter<ServerPlayer, CommandSource, MinecraftServer, BaseCommand> {
    @Override
    public PlatformPlayer<ServerPlayer> convertPlayer(ServerPlayer player) {
        return new ForgePlayer(player);
    }

    @Override
    public PlatformCommandSender<CommandSource> convertCommandSender(CommandSource commandOutput) {
        return new ForgeCommandSender(commandOutput);
    }

    @Override
    public PlatformConsole<MinecraftServer> convertConsole(MinecraftServer server) {
        return new ForgeConsole(server);
    }
}

package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import net.minecraft.server.MinecraftServer;

public class ForgeConsole extends PlatformConsole<MinecraftServer> {

    public ForgeConsole(MinecraftServer minecraftServer) {
        super(minecraftServer);
    }
}

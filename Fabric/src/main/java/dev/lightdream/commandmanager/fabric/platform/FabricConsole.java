package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import net.minecraft.server.MinecraftServer;

public class FabricConsole extends PlatformConsole<MinecraftServer> {

    public FabricConsole(MinecraftServer minecraftServer) {
        super(minecraftServer);
    }
}

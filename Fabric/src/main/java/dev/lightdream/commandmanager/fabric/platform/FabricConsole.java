package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.logger.Logger;
import net.minecraft.server.MinecraftServer;

public class FabricConsole extends PlatformConsole {

    public FabricConsole(MinecraftServer minecraftServer, FabricAdapter adapter) {
        super(minecraftServer, adapter);
    }

}

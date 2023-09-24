package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import net.minecraft.server.MinecraftServer;

public class ForgeConsole extends PlatformConsole {

    public ForgeConsole(MinecraftServer minecraftServer, ForgeAdapter adapter) {
        super(minecraftServer, adapter);
    }
}

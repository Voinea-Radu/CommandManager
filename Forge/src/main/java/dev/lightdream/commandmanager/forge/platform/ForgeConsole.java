package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformObject;
import net.minecraft.server.MinecraftServer;

public class ForgeConsole extends PlatformObject implements PlatformConsole {

    public ForgeConsole(MinecraftServer minecraftServer, ForgeAdapter adapter) {
        super(minecraftServer, adapter);
    }

    @Override
    public MinecraftServer getNative() {
        return (MinecraftServer) this.nativeObject;
    }

    @Override
    public ForgeAdapter getAdapter() {
        return (ForgeAdapter) this.adapter;
    }
}

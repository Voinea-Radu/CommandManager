package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformObject;
import net.minecraft.server.MinecraftServer;

public class FabricConsole extends PlatformObject implements PlatformConsole {

    public FabricConsole(MinecraftServer minecraftServer, FabricAdapter adapter) {
        super(minecraftServer, adapter);
    }

    @Override
    public MinecraftServer getNative() {
        return (MinecraftServer) nativeObject;
    }

    @Override
    public FabricAdapter getAdapter() {
        return (FabricAdapter) adapter;
    }

}

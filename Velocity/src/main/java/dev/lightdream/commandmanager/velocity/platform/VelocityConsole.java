package dev.lightdream.commandmanager.velocity.platform;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;

public class VelocityConsole extends PlatformConsole {

    public VelocityConsole(ConsoleCommandSource consoleCommandSender, VelocityAdapter adapter) {
        super(consoleCommandSender, adapter);
    }

    @Override
    public ConsoleCommandSource getNative() {
        return (ConsoleCommandSource) this.nativeObject;
    }

    @Override
    public VelocityAdapter getAdapter() {
        return (VelocityAdapter) this.adapter;
    }
}

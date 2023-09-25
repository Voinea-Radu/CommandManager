package dev.lightdream.commandmanager.velocity.platform;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import dev.lightdream.commandmanager.common.platform.PlatformConsole;
import dev.lightdream.commandmanager.common.platform.PlatformObject;

public class VelocityConsole extends PlatformObject implements PlatformConsole {

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

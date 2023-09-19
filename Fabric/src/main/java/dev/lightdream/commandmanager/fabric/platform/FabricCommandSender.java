package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import net.minecraft.server.command.CommandOutput;

public class FabricCommandSender extends PlatformCommandSender {
    public FabricCommandSender(CommandOutput commandSource) {
        super(commandSource);
    }
}

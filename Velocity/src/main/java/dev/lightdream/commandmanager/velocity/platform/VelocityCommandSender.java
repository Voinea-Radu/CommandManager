package dev.lightdream.commandmanager.velocity.platform;

import com.velocitypowered.api.command.CommandSource;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;

public class VelocityCommandSender extends PlatformCommandSender<CommandSource> {
    public VelocityCommandSender(CommandSource commandSource) {
        super(commandSource);
    }
}

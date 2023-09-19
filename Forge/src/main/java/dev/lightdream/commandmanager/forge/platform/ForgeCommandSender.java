package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import net.minecraft.commands.CommandSource;

public class ForgeCommandSender extends PlatformCommandSender {
    public ForgeCommandSender(CommandSource commandSource) {
        super(commandSource);
    }
}

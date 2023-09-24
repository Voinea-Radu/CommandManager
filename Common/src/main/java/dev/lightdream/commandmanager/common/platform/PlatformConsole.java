package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.logger.Logger;
import lombok.Getter;

public abstract class PlatformConsole extends PlatformCommandSender {

    public PlatformConsole(Object nativeConsole, Adapter<?, ?, ?> adapter) {
        super(nativeConsole, adapter);
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void sendMessage(String message) {
        Logger.log(message);
    }

}

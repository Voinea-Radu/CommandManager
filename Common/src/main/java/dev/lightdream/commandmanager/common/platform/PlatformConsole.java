package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.logger.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class PlatformConsole implements PlatformCommandSender {

    public final Object nativeConsole;
    private final Adapter adapter;

    public PlatformConsole(Object nativeConsole, Adapter adapter) {
        this.nativeConsole=nativeConsole;
        this.adapter=adapter;
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

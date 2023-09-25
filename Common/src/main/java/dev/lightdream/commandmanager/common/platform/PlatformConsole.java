package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.logger.Logger;

public interface PlatformConsole extends PlatformCommandSender {

    @Override
    default boolean hasPermission(String permission) {
        return true;
    }

    @Override
    default void sendMessage(String message) {
        Logger.log(message);
    }

}

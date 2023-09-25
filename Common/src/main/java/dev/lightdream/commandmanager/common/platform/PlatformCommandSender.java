package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.messagebuilder.GenericMessageBuilder;

public interface PlatformCommandSender extends IPlatformObject {

     boolean hasPermission(String permission);

     void sendMessage(String message);

    default void sendMessage(GenericMessageBuilder<?> message) {
        sendMessage(message.toString());
    }

}

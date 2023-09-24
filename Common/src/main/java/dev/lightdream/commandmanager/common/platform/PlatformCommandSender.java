package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.messagebuilder.GenericMessageBuilder;

public abstract class PlatformCommandSender extends PlatformObject {

    public PlatformCommandSender(Object nativeConsole, Adapter<?, ?, ?> adapter) {
        super(nativeConsole, adapter);
    }

    public abstract boolean hasPermission(String permission);

    public abstract void sendMessage(String message);

    public void sendMessage(GenericMessageBuilder<?> message) {
        sendMessage(message.toString());
    }


}

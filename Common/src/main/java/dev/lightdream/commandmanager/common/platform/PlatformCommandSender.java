package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.messagebuilder.GenericMessageBuilder;
import dev.lightdream.messagebuilder.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class PlatformCommandSender extends PlatformObject {

    public PlatformCommandSender(Object nativeConsole, Adapter<?, ?, ?> adapter) {
        super(nativeConsole, adapter);
    }

    public abstract boolean hasPermission(String permission);

    public abstract void sendMessage(String message);

    public void sendMessage(GenericMessageBuilder<?> message){
        sendMessage(message.toString());
    }


}

package dev.lightdream.commandmanager.common.platform;

import dev.lightdream.messagebuilder.GenericMessageBuilder;
import dev.lightdream.messagebuilder.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface PlatformCommandSender {

     boolean hasPermission(String permission);

     void sendMessage(String message);

    default void sendMessage(GenericMessageBuilder<?> message){
        sendMessage(message.toString());
    }


}

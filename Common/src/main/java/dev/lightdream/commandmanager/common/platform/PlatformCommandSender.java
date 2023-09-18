package dev.lightdream.commandmanager.common.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class PlatformCommandSender<NativeCommandSender> {

    private NativeCommandSender nativeCommandSender;

}

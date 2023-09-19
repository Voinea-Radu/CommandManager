package dev.lightdream.commandmanager.common.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class PlatformPlayer {

    private Object nativePlayer;

    public Class<?> getNativePlayerClass() {
        return nativePlayer.getClass();
    }

}

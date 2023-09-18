package dev.lightdream.commandmanager.common.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class PlatformPlayer<NativePlayer> {

    private NativePlayer nativePlayer;

    public Class<NativePlayer> getNativePlayerClass() {
        //noinspection unchecked
        return (Class<NativePlayer>) nativePlayer.getClass();
    }

}

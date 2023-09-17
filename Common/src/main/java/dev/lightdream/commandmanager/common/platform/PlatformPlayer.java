package dev.lightdream.commandmanager.common.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
public class PlatformPlayer<T> {

    private @NotNull T nativePlayer;

    @SuppressWarnings("unchecked")
    public @NotNull Class<T> getNativePlayerClass(){
        return (Class<T>) nativePlayer.getClass();
    }

}

package dev.lightdream.commandmanager.common.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class PlatformPlayer  implements PlatformCommandSender{

    private final Object nativePlayer;
    private final Adapter<?,?,?> adapter;

    public PlatformPlayer(Object nativePlayer, Adapter<?,?,?> adapter) {
        this.nativePlayer=nativePlayer;
        this.adapter=adapter;
    }

    @Override
    public boolean hasPermission(String permission) {
        return PermissionUtils.checkPermission(this, permission);
    }
}

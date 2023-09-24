package dev.lightdream.commandmanager.common.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class PlatformPlayer  extends PlatformCommandSender{

    public PlatformPlayer(Object nativePlayer, Adapter<?,?,?> adapter) {
        super(nativePlayer, adapter);
    }

    @Override
    public boolean hasPermission(String permission) {
        return PermissionUtils.checkPermission(this, permission);
    }

}

package dev.lightdream.commandmanager.common.platform;

public interface PlatformPlayer extends PlatformCommandSender {

    @Override
    default boolean hasPermission(String permission) {
        return PermissionUtils.checkPermission(this, permission);
    }

}

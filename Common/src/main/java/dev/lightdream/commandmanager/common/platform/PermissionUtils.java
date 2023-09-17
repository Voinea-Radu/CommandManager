package dev.lightdream.commandmanager.common.platform;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;

public class PermissionUtils {

    public static <T> boolean checkPermission(PlatformPlayer<T> player, String permission) {
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            PlayerAdapter<T> playerAdapter = luckPerms.getPlayerAdapter(player.getNativePlayerClass());

            return playerAdapter.getPermissionData(player.getNativePlayer()).checkPermission(permission).asBoolean();
        } catch (Exception e) {
            // No LuckPerms - has no permission
            return false;
        }
    }

}

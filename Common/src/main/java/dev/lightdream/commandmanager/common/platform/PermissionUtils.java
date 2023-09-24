package dev.lightdream.commandmanager.common.platform;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;

public class PermissionUtils {

    public static boolean checkPermission(PlatformPlayer player, String permission) {
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            //noinspection rawtypes
            PlayerAdapter playerAdapter = luckPerms.getPlayerAdapter(player.getNativePlayer().getClass());

            //noinspection unchecked
            return playerAdapter.getPermissionData(player.getNativePlayer()).checkPermission(permission).asBoolean();
        } catch (Exception e) {
            // No LuckPerms - has no permission
            return false;
        }
    }

}

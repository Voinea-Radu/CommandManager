package dev.lightdream.commandmanager.common.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;

public class PermissionUtils {

    public static boolean checkPermission(Object player, String permission) {
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            //noinspection rawtypes
            PlayerAdapter playerAdapter = luckPerms.getPlayerAdapter(player.getClass());
            //noinspection unchecked
            return playerAdapter.getPermissionData(player).checkPermission(permission).asBoolean();
        } catch (Exception e) {
            // No LuckPerms - has no permission
            return false;
        }
    }

}

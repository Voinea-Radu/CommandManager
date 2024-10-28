package com.voinearadu.commandmanager.common.utils;

import com.voinearadu.utils.logger.Logger;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;

public class CommonPermissionUtils {

    public static boolean checkPermission(Class<?> playerClass, Object player, String permission) {
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            //noinspection rawtypes
            PlayerAdapter playerAdapter = luckPerms.getPlayerAdapter(playerClass);
            //noinspection unchecked
            return playerAdapter.getPermissionData(player).checkPermission(permission).asBoolean();
        } catch (Exception error) {
            // No LuckPerms - has no permission
            Logger.error(error);
            return false;
        }
    }

}

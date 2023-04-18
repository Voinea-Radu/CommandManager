package dev.lightdream.commandmanager.fabric.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermissionUtils {

    public static boolean checkPermission(ServerPlayerEntity player, String permission) {
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            PlayerAdapter<ServerPlayerEntity> playerAdapter = luckPerms.getPlayerAdapter(ServerPlayerEntity.class);
            return playerAdapter.getPermissionData(player).checkPermission(permission).asBoolean();
        } catch (Exception e) {
            // No LuckPerms - has no permission
            return false;
        }
    }

}

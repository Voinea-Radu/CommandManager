package dev.lightdream.commandmanager.forge.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PermissionUtil {

    public static boolean checkPermission(Player player, String permission) {
        try {
            if (player instanceof ServerPlayer serverPlayer) {
                LuckPerms luckPerms = LuckPermsProvider.get();

                PlayerAdapter<ServerPlayer> playerAdapter = luckPerms.getPlayerAdapter(ServerPlayer.class);
                return playerAdapter.getPermissionData(serverPlayer).checkPermission(permission).asBoolean();
            } else {
                // Player is in SinglePlayer - No LuckPerms - has no permissions
                return false;
            }
        } catch (Exception e) {
            // No LuckPerms - has no permission
            return false;
        }
    }

}

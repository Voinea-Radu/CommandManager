package dev.lightdream.commandmanager.spigot.platform;

import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import org.bukkit.entity.Player;

public class SpigotPlayer extends PlatformPlayer<Player> {
    public SpigotPlayer(Player player) {
        super(player);
    }
}

package dev.lightdream.commandmanager.spigot.platform;

import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import org.bukkit.entity.Player;

public class SpigotPlayer extends PlatformPlayer {

    public SpigotPlayer(Player player, SpigotAdapter adapter) {
        super(player, adapter);
    }

    @Override
    public Player getNativePlayer() {
        return (Player) super.getNativePlayer();
    }

    @Override
    public void sendMessage(String message) {
        getNativePlayer().sendMessage(message);
    }
}

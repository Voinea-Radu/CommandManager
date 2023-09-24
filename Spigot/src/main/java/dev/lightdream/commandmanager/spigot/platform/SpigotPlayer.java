package dev.lightdream.commandmanager.spigot.platform;

import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import org.bukkit.entity.Player;

public class SpigotPlayer extends PlatformPlayer {

    public SpigotPlayer(Player player, SpigotAdapter adapter) {
        super(player, adapter);
    }

    @Override
    public void sendMessage(String message) {
        getNativePlayer().sendMessage(message);
    }

    @Override
    public Player getNative() {
        return (Player) this.nativeObject;
    }

    @Override
    public SpigotAdapter getAdapter() {
        return (SpigotAdapter) this.adapter;
    }
}

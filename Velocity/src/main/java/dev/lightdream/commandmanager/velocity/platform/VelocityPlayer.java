package dev.lightdream.commandmanager.velocity.platform;

import com.velocitypowered.api.proxy.Player;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import net.kyori.adventure.text.Component;

public class VelocityPlayer extends PlatformPlayer {

    public VelocityPlayer(Player player, VelocityAdapter adapter) {
        super(player, adapter);
    }

    @Override
    public void sendMessage(String message) {
        getNative().sendMessage(Component.text(message));
    }

    @Override
    public Player getNative() {
        return (Player) this.nativeObject;
    }

    @Override
    public VelocityAdapter getAdapter() {
        return (VelocityAdapter) this.adapter;
    }
}

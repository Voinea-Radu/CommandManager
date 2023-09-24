package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ForgePlayer extends PlatformPlayer {
    public ForgePlayer(ServerPlayer player, ForgeAdapter adapter) {
        super(player, adapter);
    }

    @Override
    public ServerPlayer getNativePlayer() {
        return (ServerPlayer) super.getNativePlayer();
    }

    @Override
    public void sendMessage(String message) {
        getNativePlayer().sendSystemMessage(Component.literal(message));
    }
}

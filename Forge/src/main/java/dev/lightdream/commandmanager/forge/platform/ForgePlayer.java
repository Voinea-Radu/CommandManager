package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.platform.PlatformObject;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ForgePlayer extends PlatformObject  implements PlatformPlayer {
    public ForgePlayer(ServerPlayer player, ForgeAdapter adapter) {
        super(player, adapter);
    }

    @Override
    public ServerPlayer getNative() {
        return (ServerPlayer) this.nativeObject;
    }

    @Override
    public ForgeAdapter getAdapter() {
        return (ForgeAdapter) this.adapter;
    }

    @Override
    public void sendMessage(String message) {
        getNative().sendSystemMessage(Component.literal(message));
    }
}

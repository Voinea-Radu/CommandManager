package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.common.platform.PermissionUtils;
import dev.lightdream.commandmanager.common.platform.PlatformCommandSender;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import dev.lightdream.logger.Logger;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class FabricPlayer extends PlatformPlayer {
    public FabricPlayer(ServerPlayerEntity player, FabricAdapter adapter) {
        super(player, adapter);
    }

    @Override
    public ServerPlayerEntity getNativePlayer() {
        return (ServerPlayerEntity) super.getNativePlayer();
    }

    @Override
    public void sendMessage(String message) {
        getNativePlayer().sendMessage(Text.of(message));
    }
}

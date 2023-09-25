package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.platform.PlatformObject;
import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class FabricPlayer extends PlatformObject implements PlatformPlayer {

    public FabricPlayer(ServerPlayerEntity player, FabricAdapter adapter) {
        super(player, adapter);
    }

    @Override
    public void sendMessage(String message) {
        getNative().sendMessage(Text.of(message));
    }

    @Override
    public ServerPlayerEntity getNative() {
        return (ServerPlayerEntity) this.nativeObject;
    }

    @Override
    public FabricAdapter getAdapter() {
        return (FabricAdapter) this.adapter;
    }

}

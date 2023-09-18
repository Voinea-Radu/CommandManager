package dev.lightdream.commandmanager.fabric.platform;

import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricPlayer extends PlatformPlayer<ServerPlayerEntity> {
    public FabricPlayer(ServerPlayerEntity player) {
        super(player);
    }
}

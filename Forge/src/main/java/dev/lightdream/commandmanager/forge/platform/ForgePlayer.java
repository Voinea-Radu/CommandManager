package dev.lightdream.commandmanager.forge.platform;

import dev.lightdream.commandmanager.common.platform.PlatformPlayer;
import net.minecraft.server.level.ServerPlayer;

public class ForgePlayer extends PlatformPlayer<ServerPlayer> {
    public ForgePlayer(ServerPlayer player) {
        super(player);
    }
}

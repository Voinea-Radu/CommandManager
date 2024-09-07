package com.voinearadu.commandmanager.fabric.manager;

import com.voinearadu.commandmanager.common.command.CommonCommand;
import com.voinearadu.commandmanager.common.manager.CommonCommandManager;
import com.voinearadu.commandmanager.fabric.command.FabricCommand;
import com.voinearadu.logger.Logger;
import com.voinearadu.reflections.Reflections;
import lombok.Getter;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

@Getter
public class FabricCommandManager extends CommonCommandManager {

    private final MinecraftServer server;
    private final FabricMiniMessageManager miniMessageManager;

    public FabricCommandManager(@NotNull Reflections reflections, @NotNull String basePermission,
                                @NotNull MinecraftServer server) {
        super(reflections, ServerPlayer.class, MinecraftServer.class, CommandSource.class, basePermission);

        this.server = server;
        this.miniMessageManager = new FabricMiniMessageManager(server);
    }

    @Override
    protected void platformRegister(@NotNull CommonCommand primitiveCommand) {
        FabricCommand command = (FabricCommand) primitiveCommand;

        for (String alias : command.getAliases()) {
            server.getCommands().getDispatcher().register(command.getCommandBuilder(alias));
        }

        server.getPlayerList().getPlayers().forEach(player ->
                server.getCommands().sendCommands(player)
        );
    }

    @Override
    public void sendMessage(Object target, String message) {
        if (target instanceof ServerPlayer player) {
            player.sendSystemMessage(miniMessageManager.parse(message));
            return;
        }

        if (target instanceof MinecraftServer console) {
            console.sendSystemMessage(Component.literal(message));
            return;
        }

        if (target instanceof CommandSource output) {
            output.sendSystemMessage(Component.literal(message));
            return;
        }

        Logger.error("Unknown target type: " + target.getClass().getName());
    }

    @Override
    public void broadcastMessage(String message) {
        server.getPlayerList().getPlayers().forEach(player ->
                player.sendSystemMessage(miniMessageManager.parse(message))
        );
    }

}

package com.voinearadu.commandmanager.fabric.manager;

import com.voinearadu.commandmanager.common.manager.CommonMiniMessageManager;
import net.kyori.adventure.platform.fabric.FabricAudiences;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;


public class FabricMiniMessageManager extends CommonMiniMessageManager<Component> {

    private final FabricAudiences audiences;

    public FabricMiniMessageManager(MinecraftServer server) {
        this.audiences = FabricServerAudiences.of(server);
    }

    public FabricMiniMessageManager(FabricAudiences audiences) {
        this.audiences = audiences;
    }

    @Override
    public @NotNull Component toNative(@NotNull net.kyori.adventure.text.Component component) {
        return audiences.toNative(component);
    }

    @Override
    public @NotNull String sanitize(net.kyori.adventure.text.Component component) {
        TextComponent textComponent = (TextComponent) component;
        return textComponent.content();
    }
}

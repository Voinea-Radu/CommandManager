package com.voinearadu.commandmanager.velocity.manager;

import com.voinearadu.commandmanager.common.manager.CommonMiniMessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

public class VelocityMiniMessageManager extends CommonMiniMessageManager<Component> {

    @Override
    public @NotNull Component toNative(@NotNull Component component) {
        return component;
    }

    @Override
    public @NotNull String sanitize(Component component) {
        TextComponent textComponent = (TextComponent) component;
        return textComponent.content();
    }
}

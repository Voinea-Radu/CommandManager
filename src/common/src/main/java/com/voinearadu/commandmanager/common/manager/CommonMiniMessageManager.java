package com.voinearadu.commandmanager.common.manager;

import com.voinearadu.utils.message_builder.MessageBuilder;
import com.voinearadu.utils.message_builder.MessageBuilderList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonMiniMessageManager<NativeComponent> {

    public abstract @NotNull NativeComponent toNative(@NotNull Component component);

    public @NotNull List<NativeComponent> parse(@NotNull List<String> message) {
        List<NativeComponent> output = new ArrayList<>();

        for (String line : message) {
            output.add(parse(line));
        }

        return output;
    }

    public @NotNull List<NativeComponent> parse(@NotNull MessageBuilderList message) {
        return parse(message.parse());
    }

    public @NotNull NativeComponent parse(@NotNull String message) {
        return toNative(toComponent(message));
    }

    public @NotNull NativeComponent parse(@NotNull MessageBuilder message) {
        return parse(message.parse());
    }

    public @NotNull Component toComponent(@NotNull String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }

    @SuppressWarnings("unused")
    public @NotNull Component toComponent(@NotNull MessageBuilder builder) {
        return toComponent(builder.parse());
    }

    public @NotNull String sanitize(@NotNull String text) {
        return sanitize(toComponent(text));
    }

    public abstract @NotNull String sanitize(Component component);

    public String serialize(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    @SuppressWarnings("unused")
    public TextComponent deserialize(String message) {
        return (TextComponent) parse(message);
    }
}
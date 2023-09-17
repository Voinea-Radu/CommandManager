package dev.lightdream.commandmanager.spigot;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public interface CommandMain extends CommonCommandMain<
        Player,
        CommandSender,
        ConsoleCommandSender
        > {

    Plugin getPlugin();

    @Override
    default @NotNull Set<Class<? extends ICommonCommand>> getCommandClasses() {
        return new HashSet<>();
    }

    @Override
    @NotNull Reflections getReflections();

}

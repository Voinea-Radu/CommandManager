package dev.lightdream.commandmanager.spigot;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.spigot.command.BaseCommand;
import dev.lightdream.commandmanager.spigot.platform.SpigotAdapter;
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
        ConsoleCommandSender,
        BaseCommand
        > {

    Plugin getPlugin();

    @Override
    default @NotNull Set<Class<? extends ICommonCommand>> getCommandClasses() {
        return new HashSet<>();
    }

    @Override
    @NotNull Reflections getReflections();

    @Override
    default SpigotAdapter getAdapter(){
        return new SpigotAdapter();
    }
}

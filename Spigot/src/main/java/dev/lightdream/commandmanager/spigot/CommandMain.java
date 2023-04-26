package dev.lightdream.commandmanager.spigot;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public interface CommandMain extends CommonCommandMain {

    Plugin getPlugin();

    @Override
    default @NotNull Set<Class<?>> getCommandClasses() {
        return new HashSet<>();
    }

    @Override
    @NotNull Reflections getReflections();

}

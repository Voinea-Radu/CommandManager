package dev.lightdream.commandmanager.fabric;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.command.ICommand;
import dev.lightdream.commandmanager.fabric.platform.FabricAdapter;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public interface CommandMain extends CommonCommandMain {

    @Override
    default @NotNull Set<Class<? extends ICommand>> getCommandClasses() {
        return new HashSet<>();
    }

    @Override
    @NotNull Reflections getReflections();

    MinecraftServer getServer();

    @Override
    FabricAdapter getAdapter();
}

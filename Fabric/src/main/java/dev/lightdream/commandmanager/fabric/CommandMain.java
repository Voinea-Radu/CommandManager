package dev.lightdream.commandmanager.fabric;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.fabric.command.BaseCommand;
import dev.lightdream.commandmanager.fabric.platform.FabricAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public interface CommandMain extends CommonCommandMain<
        ServerPlayerEntity,
        CommandOutput,
        MinecraftServer,
        BaseCommand
        > {

    @Override
    default @NotNull Set<Class<? extends ICommonCommand>> getCommandClasses() {
        return new HashSet<>();
    }

    @Override
    @NotNull Reflections getReflections();

    MinecraftServer getServer();

    @Override
    default FabricAdapter getAdapter() {
        return new FabricAdapter();
    }
}

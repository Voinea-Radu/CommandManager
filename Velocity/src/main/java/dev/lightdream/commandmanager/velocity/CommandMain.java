package dev.lightdream.commandmanager.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.command.ICommonCommand;
import dev.lightdream.commandmanager.velocity.command.BaseCommand;
import dev.lightdream.commandmanager.velocity.platform.VelocityAdapter;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public interface CommandMain extends CommonCommandMain {

    @NotNull ProxyServer getProxy();

    @Override
    default @NotNull Set<Class<? extends ICommonCommand>> getCommandClasses() {
        return new HashSet<>();
    }

    @Override
    @NotNull Reflections getReflections();

    @Override
    default VelocityAdapter getAdapter() {
        return new VelocityAdapter();
    }
}

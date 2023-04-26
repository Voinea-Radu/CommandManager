package dev.lightdream.commandmanager.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.lightdream.commandmanager.common.CommonCommandMain;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public interface CommandMain extends CommonCommandMain {

    @NotNull ProxyServer getProxy();

    @Override
    default @NotNull Set<Class<?>> getClasses() {
        return new HashSet<>();
    }

    @Override
    @NotNull Reflections getReflections();

}

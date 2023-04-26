package dev.lightdream.commandmanager.fabric;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public interface CommandMain extends CommonCommandMain {

    @Override
    default @NotNull Set<Class<?>> getClasses() {
        return new HashSet<>();
    }

    @Override
    @NotNull Reflections getReflections();
}

package dev.lightdream.commandmanager.forge;

import com.mojang.brigadier.CommandDispatcher;
import dev.lightdream.commandmanager.common.CommonCommandMain;
import dev.lightdream.commandmanager.common.platform.Adapter;
import dev.lightdream.commandmanager.forge.platform.ForgeAdapter;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

public interface CommandMain extends CommonCommandMain {

    @NotNull CommandDispatcher<CommandSourceStack> getDispatcher();

    @Override
    default @Nullable Reflections getReflections() {
        return null;
    }

    @Override
    default ForgeAdapter getAdapter() {
        return new ForgeAdapter();
    }
}

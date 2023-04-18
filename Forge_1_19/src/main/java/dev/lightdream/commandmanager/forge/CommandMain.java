package dev.lightdream.commandmanager.forge;

import com.mojang.brigadier.CommandDispatcher;
import dev.lightdream.commandmanager.common.CommonCommandMain;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public interface CommandMain extends CommonCommandMain {

    @NotNull CommandDispatcher<CommandSourceStack> getDispatcher();

}

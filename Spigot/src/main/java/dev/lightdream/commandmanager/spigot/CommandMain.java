package dev.lightdream.commandmanager.spigot;

import dev.lightdream.commandmanager.common.CommonCommandMain;
import org.bukkit.plugin.Plugin;

public interface CommandMain extends CommonCommandMain {

    Plugin getPlugin();

}

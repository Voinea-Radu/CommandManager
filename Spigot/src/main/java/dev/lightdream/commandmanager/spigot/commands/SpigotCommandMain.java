package dev.lightdream.commandmanager.spigot.commands;

import dev.lightdream.commandmanager.common.CommandMain;
import org.bukkit.plugin.Plugin;

public interface SpigotCommandMain extends CommandMain{

    Plugin getPlugin();

}

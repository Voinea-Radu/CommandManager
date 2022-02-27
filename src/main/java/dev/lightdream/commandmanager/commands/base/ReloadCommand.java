package dev.lightdream.commandmanager.commands.base;

import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.annotations.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@SubCommand(parent = ReloadCommand.class,
        command = "reload")
public class ReloadCommand extends dev.lightdream.commandmanager.commands.SubCommand {
    public ReloadCommand(CommandMain main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        api.loadConfigs();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        return new ArrayList<>();
    }
}

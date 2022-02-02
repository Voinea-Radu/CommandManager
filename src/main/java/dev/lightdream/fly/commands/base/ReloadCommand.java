package dev.lightdream.fly.commands.base;

import dev.lightdream.fly.CommandMain;
import dev.lightdream.fly.annotations.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@SubCommand(parent = ReloadCommand.class,
        command = "reload")
public class ReloadCommand extends dev.lightdream.fly.commands.SubCommand {
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

package dev.lightdream.fly.commands.base;

import dev.lightdream.fly.CommandMain;
import dev.lightdream.fly.annotations.SubCommand;
import dev.lightdream.messagebuilder.MessageBuilder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SubCommand(parent = VersionCommand.class,
        command = "version")
public class VersionCommand extends dev.lightdream.fly.commands.SubCommand {

    public VersionCommand(@NotNull CommandMain main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        sender.sendMessage(new MessageBuilder(api.getLang().version).addPlaceholders(new HashMap<String, String>() {{
            put("project_name", api.getProjectName());
            put("version", api.getProjectVersion());
        }}).toString());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        return new ArrayList<>();
    }
}

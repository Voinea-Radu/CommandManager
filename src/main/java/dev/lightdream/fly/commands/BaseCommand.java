package dev.lightdream.fly.commands;

import dev.lightdream.fly.CommandMain;
import dev.lightdream.fly.commands.base.ReloadCommand;
import dev.lightdream.fly.commands.base.VersionCommand;

public abstract class BaseCommand extends Command {

    public BaseCommand(CommandMain main) {
        super(main);
        subCommands.add(new ReloadCommand(main));
        subCommands.add(new VersionCommand(main));
    }


}

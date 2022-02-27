package dev.lightdream.commandmanager.commands;

import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.commands.base.ReloadCommand;
import dev.lightdream.commandmanager.commands.base.VersionCommand;

@SuppressWarnings("unused")
public abstract class BaseCommand extends Command {

    public BaseCommand(CommandMain main) {
        super(main);
        subCommands.add(new ReloadCommand(main));
        subCommands.add(new VersionCommand(main));
    }


}

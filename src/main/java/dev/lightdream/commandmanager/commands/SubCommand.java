package dev.lightdream.commandmanager.commands;

import dev.lightdream.commandmanager.CommandMain;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SubCommand extends Executable {

    public SubCommand(CommandMain main) {
        super(main);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        _execute(sender, Arrays.asList(args));
        return true;
    }


    public String getCommand() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.SubCommand.class).command();
    }

    @Override
    public String getPermission() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.SubCommand.class).permission();
    }

    @Override
    public String getUsage() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.SubCommand.class).usage();
    }

    @Override
    public boolean onlyForPlayers() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.SubCommand.class).onlyForPlayers();
    }

    @Override
    public boolean onlyForConsole() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.SubCommand.class).onlyForConsole();
    }

    @Override
    public List<String> getAliases() {
        List<String> output = new ArrayList<>(Arrays.asList(getClass().getAnnotation(dev.lightdream.commandmanager.annotations.SubCommand.class)
                .aliases()));
        output.add(getCommand());
        return output;
    }

    @Override
    public int getMinimumArgs() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.SubCommand.class).minimumArgs();
    }
}

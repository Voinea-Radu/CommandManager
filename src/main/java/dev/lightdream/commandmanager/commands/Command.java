package dev.lightdream.commandmanager.commands;

import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import dev.lightdream.messagebuilder.MessageBuilder;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Command extends Executable {

    public final CommandMain main;
    public final List<SubCommand> subCommands = new ArrayList<>();

    @SneakyThrows
    public Command(CommandMain main) {
        super(main);
        this.main = main;

        if (!getClass().isAnnotationPresent(dev.lightdream.commandmanager.annotations.Command.class)) {
            Logger.error("Class " + getClass().getSimpleName() + " is not annotated as @Command");
            return;
        }

        setName(getCommand());

        //Register the command
        Field fCommandMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
        fCommandMap.setAccessible(true);

        Object commandMapObject = fCommandMap.get(Bukkit.getPluginManager());
        if (commandMapObject instanceof CommandMap) {
            CommandMap commandMap = (CommandMap) commandMapObject;
            commandMap.register(getCommand(), this);
        } else {
            Logger.error("Command " + getCommand() + " could not be initialized");
            return;
        }
        Logger.good("Command " + getCommand() + " initialized successfully");

        //Get all the subcommands
        new Reflections("dev.lightdream").getTypesAnnotatedWith(dev.lightdream.commandmanager.annotations.SubCommand.class).forEach(aClass -> {
            if (aClass.getAnnotation(dev.lightdream.commandmanager.annotations.SubCommand.class).parent().getSimpleName().equals(getClass().getSimpleName())) {
                try {
                    Object obj;
                    Debugger.info(aClass.getSimpleName() + " constructors: ");
                    for (Constructor<?> constructor : aClass.getDeclaredConstructors()) {
                        StringBuilder parameters = new StringBuilder();
                        for (Class<?> parameter : constructor.getParameterTypes()) {
                            parameters.append(parameter.getSimpleName()).append(" ");
                        }
                        if (parameters.toString().equals("")) {
                            Debugger.info("    - zero argument");
                        } else {
                            Debugger.info("    - " + parameters);
                        }
                    }
                    if (aClass.getDeclaredConstructors()[0].getParameterCount() == 0) {
                        obj = aClass.getDeclaredConstructors()[0].newInstance();
                    } else if (aClass.getDeclaredConstructors()[0].getParameterCount() == 1) {
                        obj = aClass.getDeclaredConstructors()[0].newInstance(main);
                    } else {
                        Logger.error("Class " + aClass.getSimpleName() + " does not have a valid constructor");
                        return;
                    }

                    subCommands.add((SubCommand) obj);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void sendUsage(CommandSender sender) {
        StringBuilder helpCommandOutput = new StringBuilder();
        helpCommandOutput.append("\n");

        if (main.getLang().helpCommand.equals("")) {
            for (SubCommand subCommand : subCommands) {
                if (sender.hasPermission(subCommand.getPermission())) {
                    helpCommandOutput.append(subCommand.getCommand());
                    helpCommandOutput.append(" ");
                    helpCommandOutput.append(subCommand.getUsage());
                    helpCommandOutput.append("\n");
                }
            }
        } else {
            helpCommandOutput.append(main.getLang().helpCommand);
        }

        sender.sendMessage(new MessageBuilder(helpCommandOutput.toString()).toString());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            _execute(sender, Arrays.asList(args));
            return true;
        }

        for (SubCommand subCommand : subCommands) {
            if (!(subCommand.getAliases().contains(args[0].toLowerCase()))) {
                continue;
            }

            if (subCommand.onlyForPlayers() && !(sender instanceof Player)) {
                sender.sendMessage(new MessageBuilder(main.getLang().mustBeAPlayer).toString());
                return true;
            }

            if (subCommand.onlyForConsole() && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(new MessageBuilder(main.getLang().mustBeConsole).toString());
                return true;
            }

            if (!hasPermission(sender, subCommand.getPermission())) {
                sender.sendMessage(new MessageBuilder(main.getLang().noPermission).toString());
                return true;
            }

            subCommand.execute(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            return true;
        }

        _execute(sender, Arrays.asList(args));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String bukkitAlias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();
            for (SubCommand subCommand : subCommands) {
                for (String alias : subCommand.getAliases()) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && hasPermission(sender, subCommand.getPermission())) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }

        for (SubCommand subCommand : subCommands) {
            if (subCommand.getAliases().contains(args[0]) && hasPermission(sender, subCommand.getPermission())) {
                return subCommand.onTabComplete(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            }
        }

        return Collections.emptyList();
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return ((sender.hasPermission(permission) || permission.equalsIgnoreCase("")));
    }

    private String getCommand() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.Command.class).command();
    }

    @Override
    public String getPermission() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.Command.class).permission();
    }

    @Override
    public String getUsage() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.Command.class).usage();
    }

    @Override
    public boolean onlyForPlayers() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.Command.class).onlyForPlayers();
    }

    @Override
    public boolean onlyForConsole() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotations.Command.class).onlyForConsole();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(getClass().getAnnotation(dev.lightdream.commandmanager.annotations.Command.class).aliases());
    }

    @Override
    public int getMinimumArgs() {
        return 0;
    }
}

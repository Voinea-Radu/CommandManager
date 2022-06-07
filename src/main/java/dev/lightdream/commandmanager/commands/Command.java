package dev.lightdream.commandmanager.commands;

import dev.lightdream.commandmanager.CommandMain;
import dev.lightdream.commandmanager.Utils;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.Logger;
import dev.lightdream.messagebuilder.MessageBuilder;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Command extends org.bukkit.command.Command {

    public final CommandMain main;
    public final List<Command> subCommands = new ArrayList<>();

    @SneakyThrows
    public Command(CommandMain main) {
        super("");
        this.main = main;

        if (!getClass().isAnnotationPresent(dev.lightdream.commandmanager.annotation.Command.class)) {
            Logger.error("Class " + getClass().getName() + " is not annotated as @Command");
            return;
        }

        dev.lightdream.commandmanager.annotation.Command command = getClass().getAnnotation(dev.lightdream.commandmanager.annotation.Command.class);

        String permission = command.permission();
        if (!permission.equals("")) {
            setPermission(permission);
        }
        this.setAliases(new ArrayList<>(Arrays.asList(command.aliases())));

        if (command.parent() == Void.class) {
            registerCommand();
        }
    }

    @SneakyThrows
    private void registerCommand() {
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
        new Reflections("dev.lightdream").getTypesAnnotatedWith(dev.lightdream.commandmanager.annotation.Command.class).forEach(aClass -> {
            if (aClass.getAnnotation(dev.lightdream.commandmanager.annotation.Command.class).parent().getName().equals(getClass().getName())) {
                try {
                    Object obj;
                    Debugger.info(aClass.getName() + " constructors: ");
                    for (Constructor<?> constructor : aClass.getDeclaredConstructors()) {
                        StringBuilder parameters = new StringBuilder();
                        for (Class<?> parameter : constructor.getParameterTypes()) {
                            parameters.append(parameter.getName()).append(" ");
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
                        Logger.error("Class " + aClass.getName() + " does not have a valid constructor");
                        return;
                    }

                    subCommands.add((Command) obj);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public void sendUsage(CommandSender sender) {
        StringBuilder helpCommandOutput = new StringBuilder();
        helpCommandOutput.append("\n");

        if (main.getLang().helpCommand.equals("")) {
            for (Command subCommand : subCommands) {
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

    @SuppressWarnings("unused")
    public void exec(@NotNull CommandSender source, List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + this.getAliases().get(0) + " for " + source.getName() + ", but the command is not implemented. Exec type: CommandSender, List<String>");
        }

        source.sendMessage(Utils.listToString(getSubCommandsHelpMessage(source), "\n"));
    }

    @SuppressWarnings("unused")
    public void exec(@NotNull ConsoleCommandSender console, @NotNull List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + this.getAliases().get(0) + " for " + console.getName() + ", but the command is not implemented. Exec type: ConsoleCommandSender, List<String>");
        }

        console.sendMessage(Utils.listToString(getSubCommandsHelpMessage(console), "\n"));
    }

    @SuppressWarnings("unused")
    public void exec(@NotNull Player player, @NotNull List<String> args) {
        if (getSubCommands().size() == 0) {
            Logger.warn("Executing command " + this.getAliases().get(0) + " for " + player.getName() + ", but the command is not implemented. Exec type: Player, List<String>");
        }

        player.sendMessage(Utils.listToString(getSubCommandsHelpMessage(player), "\n"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            distributeExec(sender, new ArrayList<>(Arrays.asList(args)));
            return true;
        }

        for (Command subCommand : subCommands) {
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

            subCommand.distributeExec(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            return true;
        }

        distributeExec(sender, new ArrayList<>(Arrays.asList(args)));
        return true;
    }

    public void distributeExec(CommandSender sender, List<String> args) {
        if (onlyForPlayers()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(new MessageBuilder(main.getLang().mustBeAPlayer).parse());
                return;
            }
            exec((Player) sender, args);
            return;
        }

        if (onlyForConsole()) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(new MessageBuilder(main.getLang().mustBeConsole).parse());
                return;
            }
            exec((ConsoleCommandSender) sender, args);
            return;
        }

        exec(sender, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String bukkitAlias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();
            for (Command subCommand : subCommands) {
                for (String alias : subCommand.getAliases()) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && hasPermission(sender, subCommand.getPermission())) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }

        for (Command subCommand : subCommands) {
            if (subCommand.getAliases().contains(args[0]) && hasPermission(sender, subCommand.getPermission())) {
                return subCommand.onTabComplete(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            }
        }

        return onTabComplete(sender, new ArrayList<>(Arrays.asList(args)));
    }

    @SuppressWarnings("unused")
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        return Collections.emptyList();
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return ((sender.hasPermission(permission) || permission.equalsIgnoreCase("")));
    }

    private String getCommand() {
        return getAliases().get(0);
    }

    @Override
    public String getPermission() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotation.Command.class).permission();
    }

    @Override
    public String getUsage() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotation.Command.class).usage();
    }

    public boolean onlyForPlayers() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotation.Command.class).onlyForPlayers();
    }

    public boolean onlyForConsole() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotation.Command.class).onlyForConsole();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(getClass().getAnnotation(dev.lightdream.commandmanager.annotation.Command.class).aliases());
    }

    @SuppressWarnings("unused")
    public int getMinimumArgs() {
        return getClass().getAnnotation(dev.lightdream.commandmanager.annotation.Command.class).minimumArgs();
    }

    private List<Command> getSubCommands() {
        List<Command> subCommands = new ArrayList<>();

        new Reflections(main.getPackageName()).getTypesAnnotatedWith(dev.lightdream.commandmanager.annotation.Command.class).forEach(aClass -> {
            if (aClass.getAnnotation(dev.lightdream.commandmanager.annotation.Command.class).parent().getName().equals(getClass().getName())) {
                try {
                    Object obj;
                    Debugger.info(aClass.getName() + " constructors: ");
                    for (Constructor<?> constructor : aClass.getDeclaredConstructors()) {
                        StringBuilder parameters = new StringBuilder();
                        for (Class<?> parameter : constructor.getParameterTypes()) {
                            parameters.append(parameter.getName()).append(" ");
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
                        Logger.error("Class " + aClass.getName() + " does not have a valid constructor");
                        return;
                    }

                    subCommands.add((Command) obj);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        return subCommands;
    }

    @SuppressWarnings("unused")
    private List<String> getSubCommandsHelpMessage(CommandSender source) {
        List<String> output = new ArrayList<>();
        getSubCommands().forEach(command -> output.add(
                "/" + this.getAliases().get(0) + " " + command.getAliases().get(0) + " " +
                        command.getUsage()
        ));
        return output;
    }
}

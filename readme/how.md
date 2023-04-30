### What frameworks does this support

| Framework | Support      | Dependency Artifact                       | 
|-----------|--------------|-------------------------------------------|
| Common    | N/A          | dev.lightdrea:command-manager-common      |
| Fabric    | 1.19         | dev.lightdrea:command-manager-fabric-1-19 |
| Forge     | 1.19         | dev.lightdrea:command-manager-forge-1-19  |
| Spigot    | 1.8+         | dev.lightdrea:command-manager-spigot      |
| Sponge    | 7.4.0 (1.12) | dev.lightdrea:command-manager-sponge      |
| Velocity  | 3.1.2+       | dev.lightdrea:command-manager-velocity    |

### Creatine the main

Each framework `CommandMain` may have different requirements for what needs to be provided. Follow the JavaDocs for more
info.
This is an example with the Velocity API

```java
public class ExampleMain implements CommandMain {

    private final CommandLang lang;
    private final CommandManager commandManager;
    private final ProxyServer proxyServer;
    private final Reflections reflections;

    public ExampleMain(ProxyServer proxyServer, Reflections reflections) {
        lang = new CommandLang(); // This would usually be loaded from disk using a library like FileManager

        this.proxyServer = proxyServer;
        this.reflections = reflections;

        commandManager = new CommandManager();

        // This is an example of a command being manually registered.
        // This is not required dif you specify in the command annotation @Command(autoRegister = true)
        commandManager.registerCommand(ExampleCommand.class);
    }

    @Override
    public CommandLang getLang() {
        return lang;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public @NotNull String basePermission() {
        return "project.command";
    }

    @Override
    public @NotNull ProxyServer getProxy() {
        return proxyServer;
    }

    @Override
    public @NotNull Reflections getReflections() {
        return reflections;
    }
}

```

### How to create a command

```java

@Command(aliases = "example", autoRegister = true)
public class ExampleCommand extends BaseCommand {

    @Override
    public void exec(@NotNull CommandSource source, @NotNull List<String> args) {
        sendMessage(source, "ExampleCommand");
    }
}

```
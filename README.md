# CommandManager

![Build](../../actions/workflows/build.yml/badge.svg)
![Version](https://img.shields.io/badge/Version-4.4.10-red.svg)

# Table Of Contents

1. [Description](#description)
2. [How to add to your project](#how-to-add-to-your-project)
3. [How to use](#how-to-use)

## Description

A cross-platform command library. You can reuse the code that you already wrote for one platform to any of them with
minimal changes

## How to add to your project

The artifact can be found at the repository https://repo.lightdream.dev or https://jitpack.io (under
com.github.L1ghtDream instead of dev.lightdream)

### Maven

```xml

<repositories>
    <repository>
        <id>lightdream-repo</id>
        <url>https://repo.lightdream.dev/</url>
    </repository>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml

<dependencies>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>command-manager</artifactId>
        <version>4.4.10</version>
    </dependency>
    <dependency>
        <groupId>com.github.L1ghtDream</groupId>
        <artifactId>command-manager</artifactId>
        <version>4.4.10</version>
    </dependency>
</dependencies>
```

### Gradle - Groovy DSL

```groovy
repositories {
    maven { url "https://repo.lightdream.dev/" }
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "dev.lightdream:command-manager:4.4.10"
    implementation "com.github.L1ghtDream:command-manager:4.4.10"
}
```

### Gradle - Kotlin DSL

```kotlin
repositories {
    maven("https://repo.lightdream.dev/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("dev.lightdream:command-manager:4.4.10")
    implementation("com.github.L1ghtDream:command-manager:4.4.10")
}
```

If you want to use an older version that is not available in https://repo.lightdream.dev you can try
using https://archive-repo.lightdream.dev

## How to use

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

# LightDream (Spigot) Command Manager

![Build](https://github.com/L1ghtDream/CommandManager/actions/workflows/build.yml/badge.svg)

```xml

<repositories>
    <repository>
        <id>lightdream-repo</id>
        <url>https://repo.lightdream.dev/repository/LightDream-API/</url>
    </repository>
    <!-- Other repositories -->
</repositories>
```

```xml

<dependencies>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>DatabaseManager</artifactId>
        <version>LATEST</version>
    </dependency>
    <!-- Other dependencies -->
</dependencies>
```

## Example Main

```java
public class Example extends JavaPlugin implements CommandMain {

    private CommandLang lang;
    private FileManager fileManager;

    @Override
    public void onEnable() {
        fileManager = new FileManager(this, FileManager.PersistType.YAML);
        loadConfigs();
    }

    @Override
    public CommandLang getLang() {
        return lang;
    }

    @Override
    public void loadConfigs() {
        lang = fileManager.load(CommandLang.class);
    }

    @Override
    public String getProjectName() {
        return "Example";
    }

    @Override
    public String getProjectVersion() {
        return "1.0";
    }
}
```



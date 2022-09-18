# LightDream Command Manager

![Build](../../actions/workflows/build.yml/badge.svg)
![Version-Spigot](https://img.shields.io/badge/Version%20Spigot-2.4.0-red.svg)
![Version-Sponge](https://img.shields.io/badge/Version%20Sponge-1.3.0-red.svg)

A command registration and manager lib. Allows the creation and registration of command without interacting with either
Spigot or Sponge API.

## Use

### Maven

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
        <artifactId>CommandManager-Spigot</artifactId>
        <version>2.4.0</version>
    </dependency>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>CommandManager-Sponge</artifactId>
        <version>1.3.0</version>
    </dependency>
    <!-- Other dependencies -->
</dependencies>
```

### Gradle

```groovy
repositories {
    maven { url "https://repo.lightdream.dev/repository/LightDream-API/" }

    // Other repositories
}

dependencies {
    implementation "dev.lightdream:CommandManager-Spigot:2.4.0"
    implementation "dev.lightdream:CommandManager-Sponge:1.3.0"

    // Other dependencies
}
```

## Example

### Spigot

Can be found in the [source code](/Spigot/src/main/java/dev/lightdream/commandmanager/example)

### Sponge

Can be found in the [source code](/Sponge/src/main/java/dev/lightdream/commandmanager/example)


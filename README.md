# LightDream Command Manager

![Build](../../actions/workflows/build.yml/badge.svg)
![Version-Spigot](https://img.shields.io/badge/Version%20Spigot--red.svg)
![Version-Sponge](https://img.shields.io/badge/Version%20Sponge--red.svg)
![Version-Forge_1_19_3](https://img.shields.io/badge/Version%20Forge%201.19.3--red.svg)

A command registration and manager lib. Allows the creation and registration of command without interacting with either
Spigot or Sponge API.

## Use

<details>
  <summary>Maven</summary>

```xml

<repositories>
    <repository>
        <id>lightdream-repo</id>
        <url>https://repo.lightdream.dev/</url>
    </repository>
    <!-- Other repositories -->
</repositories>
```

```xml

<dependencies>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>CommandManager-Spigot</artifactId>
        <version></version>
    </dependency>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>CommandManager-Sponge</artifactId>
        <version></version>
    </dependency>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>CommandManager-Forge_1_19_3</artifactId>
        <version></version>
    </dependency>
    <!-- Other dependencies -->
</dependencies>
```
</details>

<details>
  <summary>Gradle</summary>

```groovy
repositories {
    maven { url "https://repo.lightdream.dev/" }

    // Other repositories
}

dependencies {
    implementation "dev.lightdream:CommandManager-Spigot:"
    implementation "dev.lightdream:CommandManager-Sponge:"
    implementation "dev.lightdream:CommandManager-Forge_1_19_3:"

    // Other dependencies
}
```
</details>

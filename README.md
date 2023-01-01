# LightDream Command Manager

![Build](../../actions/workflows/build.yml/badge.svg)
![Version-Spigot](https://img.shields.io/badge/Version%20Spigot-1-red.svg)
![Version-Sponge](https://img.shields.io/badge/Version%20Sponge-1-red.svg)
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
        <artifactId>standalone-pom</artifactId>
        <version>1</version>
    </dependency>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>standalone-pom</artifactId>
        <version>1</version>
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
    implementation "dev.lightdream:standalone-pom:1"
    implementation "dev.lightdream:standalone-pom:1"
    implementation "dev.lightdream::"

    // Other dependencies
}
```
</details>

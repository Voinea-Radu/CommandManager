# LightDream Command Manager

![Build](../../actions/workflows/build.yml/badge.svg)
![Version-Spigot](https://img.shields.io/badge/Version%20Spigot-${{ env.VERSION_SPIGOT }}-red.svg)
![Version-Sponge](https://img.shields.io/badge/Version%20Sponge-${{ env.VERSION_SPONGE }}-red.svg)
![Version-Forge_1_19_3](https://img.shields.io/badge/Version%20Forge%201.19.3-${{ env.VERSION_FORGE_1_19_3 }}-red.svg)
![Version-Velocity](https://img.shields.io/badge/Version%20Velocity-${{ env.VERSION_VELOCITY }}-red.svg)

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
        <artifactId>${{ env.ARTIFACT_SPIGOT }}</artifactId>
        <version>${{ env.VERSION_SPIGOT }}</version>
    </dependency>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>${{ env.ARTIFACT_SPONGE }}</artifactId>
        <version>${{ env.VERSION_SPONGE }}</version>
    </dependency>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>${{ env.ARTIFACT_FORGE_1_19_3 }}</artifactId>
        <version>${{ env.VERSION_FORGE_1_19_3 }}</version>
    </dependency>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>${{ env.ARTIFACT_VELOCITY }}</artifactId>
        <version>${{ env.VERSION_VELOCITY }}</version>
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
    implementation "dev.lightdream:${{ env.ARTIFACT_SPIGOT }}:${{ env.VERSION_SPIGOT }}"
    implementation "dev.lightdream:${{ env.ARTIFACT_SPONGE }}:${{ env.VERSION_SPONGE }}"
    implementation "dev.lightdream:${{ env.ARTIFACT_FORGE_1_19_3 }}:${{ env.VERSION_FORGE_1_19_3 }}"
    implementation "dev.lightdream:${{ env.ARTIFACT_VELOCITY }}:${{ env.VERSION_VELOCITY }}"

    // Other dependencies
}
```
</details>

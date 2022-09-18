# LightDream (Spigot) Command Manager

![Build](../../actions/workflows/build.yml/badge.svg)
![Version-Spigot](https://img.shields.io/badge/Version%20Spigot-${{ env.VERSION_SPIGOT }}-red.svg)
![Version-Sponge](https://img.shields.io/badge/Version%20Sponge-${{ env.VERSION_SPONGE }}-red.svg)

A command registration and manager lib. Allows the creation and registration of command without interacting with either spigot or sponge API.

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
        <artifactId>${{ env.ARTIFACT_SPIGOT }}</artifactId>
        <version>${{ env.VERSION_SPIGOT }}</version>
    </dependency>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>${{ env.ARTIFACT_SPONGE }}</artifactId>
        <version>${{ env.VERSION_SPONGE }}</version>
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
    implementation "dev.lightdream:${{ env.ARTIFACT_SPIGOT }}:${{ env.VERSION_SPIGOT }}"
    implementation "dev.lightdream:${{ env.ARTIFACT_SPONGE }}:${{ env.VERSION_SPONGE }}"

    // Other dependencies
}
```


## Example

### Spigot
Can be found in the [source code](/Spigot/src/main/java/dev/lightdream/${{ env.PACKAGE_SPIGOT }}/example)

### Sponge
Can be found in the [source code](/Sponge/src/main/java/dev/lightdream/${{ env.PACKAGE_SPONGE }}/example)


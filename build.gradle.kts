plugins {
    java
}

// Main libs versions
extra["forge"] = "44.0.41"
extra["minecraft"] = "1.19.3"
extra["parchment"] = "2022.12.18-1.19.3"
extra["velocity"] = "3.1.1"
extra["spigot-api"] = "1.8-R0.1-SNAPSHOT"
extra["spongeapi"] = "7.4.0-SNAPSHOT"

// Other
extra["lombok"] = "1.18.24"
extra["luckperms"] = "5.4"
extra["jetbrains-annotations"] = "23.1.0"

// Project version
extra["Forge_1_19_3"] = "2.0.6"
extra["Spigot"] = "3.0.6"
extra["Sponge"] = "2.0.6"
extra["Velocity"] = "2.0.7"

group = "dev.lightdream"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}

fun getVersion(id: String): String {
    return rootProject.extra[id] as String
}
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.parchmentmc.org")
        maven("https://files.minecraftforge.net/maven")
        mavenCentral()
    }
}
rootProject.name = "command-manager"

include(":command-manager-common")
project(":command-manager-common").projectDir = file("Common")

include(":command-manager-forge-1-19")
project(":command-manager-forge-1-19").projectDir = file("Forge")

include(":command-manager-spigot")
project(":command-manager-spigot").projectDir = file("Spigot")

include(":command-manager-velocity")
project(":command-manager-velocity").projectDir = file("Velocity")

include(":command-manager-fabric-1-20")
project(":command-manager-fabric-1-20").projectDir = file("Fabric")

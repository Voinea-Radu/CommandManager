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
rootProject.name = "CoomandManager"
//include("Common")
include("Forge_1_19_3")
include("Spigot")
include("Sponge")
include("Common")
include("Velocity")

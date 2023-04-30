plugins {
    java
}

group = "dev.lightdream"
version = libs.versions.project.get()

dependencies {

}

subprojects{
    repositories {
        mavenCentral()
        maven ("https://repo.papermc.io/repository/maven-public/")
        maven ("https://repo.spongepowered.org/maven/")
        maven ("https://repo.lightdream.dev/")
        maven ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://cursemaven.com") {
            content {
                includeGroup("curse.maven")
            }
        }
        maven("https://maven.parchmentmc.org")
    }
}
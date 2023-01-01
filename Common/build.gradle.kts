plugins {
    id("java")
}

group = "com.pokeninjas"
version = "1.0.0"

repositories {
    maven ("https://repo.spongepowered.org/maven/")
    maven ("https://repo.lightdream.dev/")
    maven ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    // LightDream
    implementation("dev.lightdream:Logger:${getVersion("Logger")}"){
        exclude("org.projectlombok")
    }
    implementation("dev.lightdream:Lambda:${getVersion("Lambda")}"){
        exclude("org.projectlombok")
    }
    implementation("dev.lightdream:MessageBuilder:${getVersion("MessageBuilder")}"){
        exclude("org.projectlombok")
    }

    // Lombok
    compileOnly("org.projectlombok:lombok:${getVersion("lombok")}")
    annotationProcessor("org.projectlombok:lombok:${getVersion("lombok")}")

    // Utils
    implementation("org.reflections:reflections:${getVersion("reflections")}")
}

fun getVersion(id: String): String {
    return rootProject.extra[id] as String
}

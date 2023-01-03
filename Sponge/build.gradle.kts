plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

group = "dev.lightdream"
version = "1.5.2"


repositories {
    maven ("https://repo.spongepowered.org/maven/")
    maven ("https://repo.lightdream.dev/")
}

dependencies {
    // Sponge
    implementation("org.spongepowered:spongeapi:${getVersion("spongeapi")}")

    // Project
    implementation(project(":Common")){
        exclude("org.projectlombok")
    }

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

tasks {
    shadowJar {
        isZip64 = true
        archiveFileName.set("Sponge.jar")
        dependencies {
            include(project(":Common"))
        }
    }
}

tasks.getByName("jar").finalizedBy("shadowJar")

fun getVersion(id: String): String {
    return rootProject.extra[id] as String
}

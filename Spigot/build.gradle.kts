buildscript {
    dependencies {
        classpath("org.hibernate.build.gradle:gradle-maven-publish-auth:2.0.1")
    }
    repositories {
        maven("https://repository.jboss.org/nexus/content/groups/public/")
        mavenLocal()
        mavenCentral()
    }
}

plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

group = "dev.lightdream"
version = "2.6.0"

repositories {
    maven ("https://repo.spongepowered.org/maven/")
    maven ("https://repo.lightdream.dev/")
    maven ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    // Sponge
    implementation("org.spigotmc:spigot-api:${getVersion("spigot-api")}")

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
        archiveFileName.set("Spigot.jar")
        dependencies {
            include(project(":Common"))
        }
    }
}

tasks.getByName("jar").finalizedBy("shadowJar")


fun getVersion(id: String): String {
    return rootProject.extra[id] as String
}

plugins {
    id("java")
}

group = "dev.lightdream"
version = "1.0.0"

repositories {
    maven ("https://repo.spongepowered.org/maven/")
    maven ("https://repo.lightdream.dev/")
    maven ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}


dependencies {
    // LightDream
    implementation("dev.lightdream:logger:+"){
        exclude("org.projectlombok")
    }
    implementation("dev.lightdream:lambda:+"){
        exclude("org.projectlombok")
    }
    implementation("dev.lightdream:message-builder:+"){
        exclude("org.projectlombok")
    }
    implementation("dev.lightdream:reflections:+"){
        exclude("org.projectlombok")
    }

    // Lombok
    compileOnly("org.projectlombok:lombok:${getVersion("lombok")}")
    annotationProcessor("org.projectlombok:lombok:${getVersion("lombok")}")

    // JetBrains
    compileOnly("org.jetbrains:annotations:23.1.0")
    annotationProcessor("org.jetbrains:annotations:23.1.0")
}

fun getVersion(id: String): String {
    return rootProject.extra[id] as String
}

configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(10, "seconds")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Jar> {
    archiveFileName.set("${rootProject.name}.jar")
}